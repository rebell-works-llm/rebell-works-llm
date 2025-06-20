package com.rebellworksllm.backend.vacancies.application;

import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.vacancies.data.PineconeService;
import com.rebellworksllm.backend.vacancies.domain.ScoredVacancy;
import com.rebellworksllm.backend.vacancies.domain.Vacancy;
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

        List<ScoredVacancy> matches = pineconeService.queryTopMatches(student.embeddingResult().embeddings(), 1);
        Vacancy vacancy = matches.getFirst().vacancy();

        assertEquals(1, matches.size());
        assertFalse(vacancy.id().isEmpty());
        assertFalse(vacancy.title().isEmpty());
        assertFalse(vacancy.description().isEmpty());
    }
}
