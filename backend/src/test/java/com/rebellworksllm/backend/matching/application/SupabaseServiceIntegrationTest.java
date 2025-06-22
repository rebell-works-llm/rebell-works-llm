package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.modules.vacancies.data.SupabaseResponse;
import com.rebellworksllm.backend.modules.vacancies.data.SupabaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SupabaseServiceIntegrationTest {

    @Autowired
    private SupabaseService supabaseService;

    @Test
    void testGetVacancyById_returnsCorrectResponse() {
        String vacancyId = "99f5f005-fae0-4f97-af79-f2e6955c7e86";

        SupabaseResponse response = supabaseService.getVacancyById(vacancyId);

        assertNotNull(response);
        assertEquals("id", response.id());
        assertEquals(1.0, response.priority());
        assertEquals(1, response.matchCount());
    }

    @Test
    void testUpdateMatchCount_updatesSuccessfully() {
        String vacancyId = "99f5f005-fae0-4f97-af79-f2e6955c7e86";

        assertDoesNotThrow(() -> supabaseService.updateMatchCount(vacancyId));
    }

    @TestConfiguration
    static class MockRestTemplateConfig {

        @Bean
        @Primary
        @Qualifier("supabaseRestTemplate")
        public RestTemplate restTemplate() {
            RestTemplate mockTemplate = Mockito.mock(RestTemplate.class);

            SupabaseResponse mockResponse = new SupabaseResponse("id", 1.0, 1, "link", "title");
            SupabaseResponse[] body = { mockResponse };

            Mockito.when(mockTemplate.getForEntity(Mockito.any(), Mockito.eq(SupabaseResponse[].class)))
                    .thenReturn(ResponseEntity.ok(body));

            return mockTemplate;
        }
    }
}
