package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.domain.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Qualifier("cosSimMatchEngine")
public class CosSimMatchEngine implements MatchEngine {

    private final StudentJobMatchingService matchingService;
    private final VacancyService vacancyService;

    public CosSimMatchEngine(StudentJobMatchingService matchingService, VacancyService vacancyService) {
        this.matchingService = matchingService;
        this.vacancyService = vacancyService;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {
        try {
            List<Vacancy> vacancies = vacancyService.getAllVacancies();
            List<StudentVacancyMatch> matches = matchingService.findBestMatches(student, vacancies, amount);
            return Collections.unmodifiableList(matches);
        } catch (Exception e) {
            throw new MatchingException("Cosinus similarity matching failed", e);
        }
    }
}
