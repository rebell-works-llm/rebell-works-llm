package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.VacancyQueryService;
import com.rebellworksllm.backend.matching.application.dto.VacancyMatchDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/matcher")
public class SearchQueryController {

    private final VacancyQueryService queryService;

    public SearchQueryController(VacancyQueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<VacancyMatchDto> receiveQueryResponse(@RequestBody VacancyQueryDto requests) {
        VacancyMatchDto result = queryService.processQuery(requests);
        return ResponseEntity.ok(result);
    }
}
