package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2) {
        return List.of(
                check(candidateName),
                check(vac1.title()), check(vac1.description()), check(vac1.workingHours()),
                check(vac1.salary()), check(vac1.function()),
                check(vac2.title()), check(vac2.description()), check(vac2.workingHours()),
                check(vac2.salary()), check(vac2.function())
        );
    }

    private String check(String input) {
        return (input == null || input.trim().isEmpty())
                ? "Dit veld is nog unknown at the moment"
                : input.trim();
    }
}
