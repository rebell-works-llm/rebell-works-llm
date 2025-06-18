package com.rebellworksllm.backend.matching.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class HubSpotWebhookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private HubSpotWebhookService webhookService;

}
