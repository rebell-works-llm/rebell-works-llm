package com.rebellworksllm.backend.vacancies.application;

import com.rebellworksllm.backend.vacancies.domain.ScoredVacancy;
import com.rebellworksllm.backend.vacancies.domain.Vacancy;

import java.util.List;

public interface VacancyProvider {

    List<ScoredVacancy> getVacanciesBySimilarity(List<Double> vector, int topK);

    Vacancy getVacancyById(int id);

    List<Vacancy> getVacanciesByIds(List<Integer> ids);

}
