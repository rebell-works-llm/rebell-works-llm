package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CosinusSimularityMatchingServiceTest {

    private CosSimStudentJobMatchingService matchingService;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @BeforeEach
    void setUp() {
        matchingService = new CosSimStudentJobMatchingService();
    }

    @Test
    void testMatchingService() {
        // Arrange
        List<Double> matchingVec = List.of(2.0, 2.0);
        Student student = new Student("Game Job",  matchingVec);
        List<Vacancy> vacancies = List.of(
                new Vacancy("Software Intern Vacancy", List.of(1.0, 2.0)),
                new Vacancy("Createve Intern Vacancy", List.of(2.0, 3.0)),
                new Vacancy("Game Development Intern", matchingVec)
        );

        // Act
        List<StudentVacancyMatch> foundMatches = matchingService.findBestMatches(student, vacancies, 1);

        // Assert
        assertEquals(1, foundMatches.size());
        assertEquals(matchingVec, foundMatches.getFirst().vacancy().vector());
        assertEquals("1,00", df.format(foundMatches.getFirst().matchScore()));
    }
}
