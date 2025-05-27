package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DummyVacanyServiceTest {

    @Test
    void testGetVacancies() {
        DummyVacancyService service = new DummyVacancyService();

        List<Vacancy> vacancies = service.getAllVacancies();
        Vacancy vacancy = vacancies.getFirst();

        assertEquals(10, vacancies.size());
        assertEquals("Creative Marketing Intern", vacancy.title());
        assertEquals(1536, vacancy.embeddingResult().embeddings().size());
    }
}
