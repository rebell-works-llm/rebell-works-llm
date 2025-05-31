package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
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
        EmbeddingResult matchingVec = new EmbeddingResult(List.of(2.0, 2.0));
        Student student = createStudent(matchingVec);
        List<Vacancy> vacancies = List.of(
                new Vacancy("1", "Software Intern Vacancy", "website", new EmbeddingResult(List.of(1.0, 2.0))),
                new Vacancy("2", "Createve Intern Vacancy", "website", new EmbeddingResult(List.of(2.0, 3.0))),
                new Vacancy("3", "Game Development Intern", "website", matchingVec)
        );

        // Act
        List<StudentVacancyMatch> foundMatches = matchingService.findBestMatches(student, vacancies, 1);

        // Assert
        assertEquals(1, foundMatches.size());
        assertEquals(matchingVec, foundMatches.getFirst().vacancy().embeddingResult());
        assertEquals("1,00", df.format(foundMatches.getFirst().matchScore()));
    }
}
