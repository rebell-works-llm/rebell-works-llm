package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Vacancy;

import java.util.List;

public interface TemplateService {
    List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2);
}
