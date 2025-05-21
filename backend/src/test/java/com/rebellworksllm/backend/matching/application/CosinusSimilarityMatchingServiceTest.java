package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.embedding.domain.Vectors;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.List;

import static com.rebellworksllm.backend.matching.application.StudentFactory.createStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CosinusSimilarityMatchingServiceTest {

    private CosSimStudentJobMatchingService matchingService;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @BeforeEach
    void setUp() {
        matchingService = new CosSimStudentJobMatchingService();
    }

    @Test
    void testMatchingService() {
        // Arrange
        Vectors matchingVec = new Vectors(List.of(2.0, 2.0));
        Student student = createStudent(matchingVec);
        List<Vacancy> vacancies = List.of(
                new Vacancy("Software Intern Vacancy", "website", new Vectors(List.of(1.0, 2.0)), 0.0, 0),
                new Vacancy("Createve Intern Vacancy", "website", new Vectors(List.of(2.0, 3.0)), 0.0, 0),
                new Vacancy("Game Development Intern", "website", matchingVec, 1.0, 2)
        );

        // Act
        List<StudentVacancyMatch> foundMatches = matchingService.findBestMatches(student, vacancies, 1);

        // Assert
        assertEquals(1, foundMatches.size());
        assertEquals(matchingVec, foundMatches.getFirst().vacancy().vectors());
        assertEquals("2,00", df.format(foundMatches.getFirst().matchScore()));
    }
}
