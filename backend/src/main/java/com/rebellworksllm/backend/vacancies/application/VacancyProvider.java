package com.rebellworksllm.backend.vacancies.application;

import com.rebellworksllm.backend.vacancies.application.dto.MatchedVacancy;
import com.rebellworksllm.backend.vacancies.application.dto.VacancyResponseDto;

import java.util.List;

public interface VacancyProvider {

    List<MatchedVacancy> getVacanciesBySimilarity(List<Double> vector, int topK);

    VacancyResponseDto getVacancyById(String id);

    boolean incrementMatchCount(String id);
}
