package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import com.rebellworksllm.backend.matching.presentation.QueryRequestsDto;
import org.springframework.stereotype.Service;

@Service
public class DummyQueryService implements QueryService {

    @Override
    public QueryResponseDto processQuery(QueryRequestsDto request) {
        String studentPhoneNumber = request.phoneNumber();
        String studentMessageText = request.messageText();

        System.out.println("Simulated WhatsApp Message from " + studentPhoneNumber + ": " + studentMessageText);

        // TODO: Validate and Fetch Student Data
        // TODO: Fetch Vacancy Data as Vector(s)
        // TODO: Match Student With Vacancies (GPT)

        String mathResult = "Stage Junior Dev | Hogeschool Utrecht | Start 3-4-2025";

        // Return found match results
        // TODO: Return error if no matches found
        return QueryResponseDto.fromVacancy(mathResult);
    }
}
