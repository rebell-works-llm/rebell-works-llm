package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import com.rebellworksllm.backend.matching.presentation.QueryRequestsDto;
import com.rebellworksllm.backend.student.application.StudentDto;
import com.rebellworksllm.backend.student.application.StudentService;
import org.springframework.stereotype.Service;

@Service
public class DummyQueryService implements QueryService {

    private final StudentService studentService;

    public DummyQueryService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public QueryResponseDto processQuery(QueryRequestsDto request) {
        String studentPhoneNumber = request.phoneNumber();
        String studentMessageText = request.messageText();

        System.out.println("Simulated WhatsApp Message from " + studentPhoneNumber + ": " + studentMessageText);

        // Fetch the student or throw if not found
        StudentDto studentDto = studentService.findByPhoneNumber(studentPhoneNumber);

        // TODO: Fetch Vacancy Data as Vector(s)
        // TODO: Match Student With Vacancies (GPT)

        // TODO: Construct a fancy response message to send the best matches to the student
        String mathResult = "Great to hear " + studentDto.name() + "!. I found a match for you: Junior Software Developer at the Hogeschool Utrecht, starting april 3.\n\nDoes this sound good to you?";

        // TODO: Return error if no matches found
        return QueryResponseDto.fromVacancy(mathResult);
    }
}
