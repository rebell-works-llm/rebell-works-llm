package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import com.rebellworksllm.backend.matching.presentation.QueryRequestsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DummyQueryService implements QueryService {

    private final VacancyService vacancyService;
    private final TextEmbedder textEmbedder;
    private final StudentJobMatchingService studentJobMatchingService;

    public DummyQueryService(VacancyService vacancyService,
                             TextEmbedder textEmbedder,
                             StudentJobMatchingService studentJobMatchingService) {
        this.vacancyService = vacancyService;
        this.textEmbedder = textEmbedder;
        this.studentJobMatchingService = studentJobMatchingService;
    }

    @Override
    public QueryResponseDto processQuery(QueryRequestsDto request) {
        Student student = new Student(request.messageText(), textEmbedder.embedText(request.messageText()));
        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        List<StudentVacancyMatch> matches = studentJobMatchingService.findBestMatches(
                student,
                vacancies,
                5
        );

        // TODO: Construct a fancy response message to send the best matches to the student
        // TODO: Return error if no matches found
        return QueryResponseDto.fromVacancy(
                matches.stream()
                        .map(match -> String.valueOf(match.vacancy().title()))
                        .toList()
        );
    }
}
