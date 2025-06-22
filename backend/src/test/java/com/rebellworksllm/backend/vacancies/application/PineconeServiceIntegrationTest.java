package com.rebellworksllm.backend.vacancies.application;

import com.rebellworksllm.backend.modules.matching.domain.Student;
import com.rebellworksllm.backend.modules.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.modules.vacancies.application.dto.MatchedVacancy;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.vacancies.data.PineconeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.rebellworksllm.backend.matching.application.utils.DummyEmbeddings.DUMMY_EMBEDDING;
import static com.rebellworksllm.backend.matching.application.StudentFactory.createStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class PineconeServiceIntegrationTest {

    @Qualifier("pineconeRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    private PineconeService pineconeService;

    private Student student;

    @BeforeEach
    void setUp() {
        pineconeService = new PineconeService(restTemplate);
        student = createStudent(new EmbeddingResult(DUMMY_EMBEDDING));
    }

    @Test
    void testQueryTopMatches() {

        List<MatchedVacancy> matches = pineconeService.queryTopMatches(student.embeddingResult().embeddings(), 1);
        VacancyResponseDto vacancyResult = matches.getFirst().vacancyResponse();

        assertEquals(1, matches.size());
        assertFalse(vacancyResult.id().isEmpty());
        assertFalse(vacancyResult.title().isEmpty());
        assertFalse(vacancyResult.description().isEmpty());
    }
}
