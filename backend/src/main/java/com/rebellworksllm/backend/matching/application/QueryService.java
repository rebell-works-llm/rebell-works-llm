package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import com.rebellworksllm.backend.matching.presentation.QueryRequestsDto;

public interface QueryService {

    QueryResponseDto processQuery(QueryRequestsDto request);
}
