package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HubSpotWebhookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HubSpotWebhookService webhookService;

    @Test
    @DisplayName("Cannot handle student matching when no matches found")
    void matchingReturnsNoMatches() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/hubspot/contacts/created")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"objectId\": \"" + "abc" + "\"}");

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}
