package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.domain.QueryService;
import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/matcher")
public class SearchQueryController {

    private final QueryService queryService;

    public SearchQueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<QueryResponseDto> receiveQueryResponse(@RequestBody QueryRequestsDto requests) {
        QueryResponseDto result = queryService.processQuery(requests);
        return ResponseEntity.ok(result);
    }
}
