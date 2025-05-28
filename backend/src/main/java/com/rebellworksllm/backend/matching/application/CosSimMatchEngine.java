package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CosSimMatchEngine implements MatchEngine {

    private final StudentJobMatchingService matchingService;
    private final VacancyService vacancyService;

    public CosSimMatchEngine(StudentJobMatchingService matchingService, VacancyService vacancyService) {
        this.matchingService = matchingService;
        this.vacancyService = vacancyService;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {

        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        List<StudentVacancyMatch> matches = matchingService.findBestMatches(student, vacancies, amount);

        return Collections.unmodifiableList(matches);
    }
}
