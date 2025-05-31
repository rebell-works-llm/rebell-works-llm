package com.rebellworksllm.backend.matching.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.rebellworksllm.backend.utils.HttpTestUtils.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HubSpotWebhookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private HubSpotWebhookService webhookService;

    @Test
    @DisplayName("Cannot handle student matching when no matches found")
    void matchingReturnsNoMatches() throws Exception {
        String body = "{\"objectId\": \"abc\"}";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = generateSignature("POST", "http://localhost/api/v1/hubspot/contacts/created", body, timestamp);

    }
}
