package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.openai.presentation.dto.EmbeddingResult;
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

        List<StudentVacancyMatch> matches = pineconeService.queryTopMatches(student, 1);

        assertEquals(1, matches.size());
        assertFalse(matches.getFirst().vacancy().title().isEmpty());
        assertFalse(matches.getFirst().vacancy().website().isEmpty());
    }
}
