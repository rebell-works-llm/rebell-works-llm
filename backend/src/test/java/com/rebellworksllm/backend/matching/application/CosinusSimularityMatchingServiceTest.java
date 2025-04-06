package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CosinusSimularityMatchingServiceTest {

    private CosinusSimulariyMatchingService matchingService;

    @BeforeEach
    void setUp() {
        matchingService = new CosinusSimulariyMatchingService();
    }

    @Test
    void testMatchingService() {
        float[] vec1 = new float[2];
        vec1[0] = 1.0f;
        vec1[1] = 2.0f;

        float[] vec2 = new float[2];
        vec2[0] = 2.0f;
        vec2[1] = 3.0f;

        float[] vec3 = new float[2];
        vec3[0] = 2.0f;
        vec3[1] = 2.0f;

        List<Vacancy> vacancies = List.of(
                new Vacancy("Software Intern Vacancy", vec1),
                new Vacancy("Createve Intern Vacancy", vec2),
                new Vacancy("Game Development Intern", vec3)
        );


        float[] studVec = new float[2];
        studVec[0] = 2.0f;
        studVec[1] = 2.0f;
        List<StudentVacancyMatch> foundMatches = matchingService.findBestMatches(studVec, vacancies, 2);
        for (StudentVacancyMatch match : foundMatches) {
            System.out.println(match.vacancy().getTitle() + " : " + match.matchScore());
        }

        assertEquals(1.0, Math.round(foundMatches.getFirst().matchScore()));
        assertEquals(2, foundMatches.size());
    }
}
