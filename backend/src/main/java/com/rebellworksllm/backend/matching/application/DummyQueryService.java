package com.rebellworksllm.backend.matching.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import com.rebellworksllm.backend.matching.presentation.QueryRequestsDto;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;


import java.io.File;
import java.util.ArrayList;
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
        Match bestMatch = getMatchByTitle(matches.getFirst().vacancy().title());
        List<Match> otherMatches = new ArrayList<>();
        for(int i = 1; i < 5; i++) {
            otherMatches.add(getMatchByTitle(matches.get(i).vacancy().title()));
        }

        // TODO: Construct a fancy response message to send the best matches to the student
        // TODO: Return error if no matches found
        return QueryResponseDto.fromVacancy(bestMatch, otherMatches);
    }

    public Match getMatchByTitle(String title) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new ClassPathResource("vector-vacancies.json").getFile();
            JsonNode root = mapper.readTree(file);
            JsonNode vacancies = root.get("dummy-vacancies");

            for (JsonNode vacancyNode : vacancies) {
                String nodeTitle = vacancyNode.get("title").asText();
                if (nodeTitle.equalsIgnoreCase(title)) {
                    String website = vacancyNode.get("website").asText();
                    return new Match(website);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
