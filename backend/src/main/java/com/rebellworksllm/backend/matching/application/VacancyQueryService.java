package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.VacancyMatchDto;
import com.rebellworksllm.backend.matching.presentation.VacancyQueryDto;

public interface VacancyQueryService {

    VacancyMatchDto processQuery(VacancyQueryDto request);
}
