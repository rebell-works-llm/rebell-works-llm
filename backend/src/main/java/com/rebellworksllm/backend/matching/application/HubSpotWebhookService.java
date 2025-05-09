package com.rebellworksllm.backend.matching.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import com.rebellworksllm.backend.embedding.domain.Vectors;
import com.rebellworksllm.backend.matching.application.dto.VacancyMatchDto;
import com.rebellworksllm.backend.matching.application.dto.MatchResponseDto;
import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.matching.presentation.HubSpotWebhookPayload;
import com.rebellworksllm.backend.whatsapp.application.WhatsAppService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
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

    public void matchStudent(HubSpotWebhookPayload payload) {
        // Setup student match object
        StudentDto studentDto = studentService.getStudentById(payload.objectId());
        Vectors studentVectors = textEmbedder.embedText(
                studentDto.study() + " " + studentDto.studyLocation() + " " + studentDto.text()
        );
        Student student = new Student(
                studentDto.fullName(),
                studentDto.email(),
                studentDto.phoneNumber(),
                studentDto.study(),
                studentDto.text(),
                studentDto.studyLocation(),
                studentVectors
        );

        // Setup vacancy match objects
        List<Vacancy> vacancies = vacancyService.getAllVacancies();

        // Match student and vacancies
        List<StudentVacancyMatch> matches = studentJobMatchingService.findBestMatches(student, vacancies, 5);

        VacancyMatchDto bestMatch = getMatchByTitle(matches.getFirst().vacancy().title());
        List<VacancyMatchDto> otherMatches = new ArrayList<>();
        for(int i = 1; i < 5; i++) {
            otherMatches.add(getMatchByTitle(matches.get(i).vacancy().title()));
        }

        MatchResponseDto matchResponse = MatchResponseDto.fromVacancy(bestMatch, otherMatches);

        // Send WhatsApp message
        String response = whatsAppService.sendWithVacancyTemplate(
                student.phoneNumber(),
                student.name(),
                bestMatch.website(),
                otherMatches.get(0).website(),
                otherMatches.get(1).website(),
                otherMatches.get(2).website(),
                otherMatches.get(3).website()
        );
    }

    public VacancyMatchDto getMatchByTitle(String title) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new ClassPathResource("vector-vacancies.json").getFile();
            JsonNode root = mapper.readTree(file);
            JsonNode vacancies = root.get("dummy-vacancies");

            for (JsonNode vacancyNode : vacancies) {
                String nodeTitle = vacancyNode.get("title").asText();
                if (nodeTitle.equalsIgnoreCase(title)) {
                    String website = vacancyNode.get("website").asText();
                    return new VacancyMatchDto(nodeTitle, website);
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
