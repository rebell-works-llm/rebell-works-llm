package com.rebellworksllm.backend.modules.vacancies.application;

import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.vacancies.application.dto.MatchedVacancy;

import java.util.List;

public interface VacancyProvider {

    List<MatchedVacancy> getVacanciesBySimilarity(List<Double> vector, int topK);

    VacancyResponseDto getVacancyById(String id);

    void incrementMatchCount(String id);
}
