package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import com.rebellworksllm.backend.embedding.domain.Vectors;
import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.whatsapp.application.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubSpotWebhookService {

    private final StudentService studentService;
    private final VacancyService vacancyService;
    private final StudentJobMatchingService studentJobMatchingService;
    private final TextEmbedder textEmbedder;
    private final WhatsAppService whatsAppService;

    public HubSpotWebhookService(StudentService studentService,
                                 VacancyService vacancyService,
                                 StudentJobMatchingService studentJobMatchingService,
                                 TextEmbedder textEmbedder,
                                 WhatsAppService whatsAppService) {
        this.studentService = studentService;
        this.vacancyService = vacancyService;
        this.studentJobMatchingService = studentJobMatchingService;
        this.textEmbedder = textEmbedder;
        this.whatsAppService = whatsAppService;
    }

    public void matchStudent(long id, int matchLimits) {
        StudentDto studentDto = studentService.getStudentById(id);
        Student student = toStudent(studentDto);
        matchAndNotifyStudent(student, matchLimits);
    }

    public void matchStudent(StudentDto studentDto, int matchLimits) {
        Student student = toStudent(studentDto);
        matchAndNotifyStudent(student, matchLimits);
    }

    private Student toStudent(StudentDto studentDto) {
        Vectors studentVectors = textEmbedder.embedText(
                studentDto.study() + " " + studentDto.studyLocation() + " " + studentDto.text()
        );
        return new Student(
                studentDto.fullName(),
                studentDto.email(),
                studentDto.phoneNumber(),
                studentDto.study(),
                studentDto.text(),
                studentDto.studyLocation(),
                studentVectors
        );
    }

    public void matchAndNotifyStudent(Student student, int matchLimits) {
        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        List<StudentVacancyMatch> matches = studentJobMatchingService.findBestMatches(student, vacancies, matchLimits);

        whatsAppService.sendWithVacancyTemplate(
                student.phoneNumber(),
                student.name(),
                matches.getFirst().vacancy().website(),
                matches.get(0).vacancy().website(),
                matches.get(1).vacancy().website(),
                matches.get(2).vacancy().website(),
                matches.get(3).vacancy().website()
        );
    }
}
