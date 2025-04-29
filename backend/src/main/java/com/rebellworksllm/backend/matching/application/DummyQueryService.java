package com.rebellworksllm.backend.matching.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import com.rebellworksllm.backend.matching.presentation.QueryRequestsDto;
import com.rebellworksllm.backend.whatsapp.application.WhatsAppService;
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
    private final WhatsAppService whatsAppService;

    public DummyQueryService(VacancyService vacancyService,
                             TextEmbedder textEmbedder,
                             StudentJobMatchingService studentJobMatchingService,
                             WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
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

        QueryResponseDto queryResponseDto = QueryResponseDto.fromVacancy(bestMatch, otherMatches);

        // TODO: Construct a fancy response message to send the best matches to the student
        String response = whatsAppService.sendWithVacancyTemplate(
                request.phoneNumber(),
                request.name(),
                bestMatch.getWebsite(),
                otherMatches.get(0).getWebsite(),
                otherMatches.get(1).getWebsite(),
                otherMatches.get(2).getWebsite(),
                otherMatches.get(3).getWebsite()
        );

        // TODO: Return error if no matches found
        return queryResponseDto;
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
