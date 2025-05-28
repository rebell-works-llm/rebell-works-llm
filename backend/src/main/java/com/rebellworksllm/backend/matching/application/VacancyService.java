package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Vacancy;

import java.util.List;

public interface VacancyService {

    List<Vacancy> getAllVacancies();
}
