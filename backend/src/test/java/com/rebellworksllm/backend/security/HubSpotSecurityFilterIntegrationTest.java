package com.rebellworksllm.backend.security;

import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.rebellworksllm.backend.utils.HttpTestUtils.generateSignature;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HubSpotSecurityFilterIntegrationTest {

    private final static String CONTACT_CREATION_URL = "http://localhost/api/v1/hubspot/contacts/created";

    @Autowired
    private MockMvc mockMvc;

    @Value("${hubspot.client-secret}")
    private String secret;

    @MockitoBean
    private HubSpotWebhookService hubSpotWebhookService;

    @Test
    @DisplayName("Should allow valid request")
    void shouldAllowValidRequest() throws Exception {
        String body = "{\"objectId\": \"12345\"}";
        String method = "POST";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = generateSignature(method, CONTACT_CREATION_URL, body, timestamp, secret);
        System.out.println(signature);

        mockMvc.perform(post(CONTACT_CREATION_URL)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-HubSpot-Request-Timestamp", timestamp)
                        .header("X-HubSpot-Signature-v3", signature))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should not allow invalid request")
    void shouldNotAllowInvalidRequest() throws Exception {
        String body = "{\"objectId\": \"12345\"}";
        String timestamp = String.valueOf(System.currentTimeMillis());

        mockMvc.perform(post(CONTACT_CREATION_URL)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-HubSpot-Request-Timestamp", timestamp)
                        .header("X-HubSpot-Signature-v3", "invalid-signature"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should not allow missing signature header")
    void testMissingSignatureHeader() throws Exception {
        mockMvc.perform(post(CONTACT_CREATION_URL)
                        .content("{\"objectId\": \"12345\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-HubSpot-Request-Timestamp", String.valueOf(System.currentTimeMillis())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should not allow missing timestamp header")
    void testMissingTimestampHeader() throws Exception {
        String body = "{\"objectId\": \"12345\"}";
        String method = "POST";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = generateSignature(method, CONTACT_CREATION_URL, body, timestamp, secret);

        mockMvc.perform(post(CONTACT_CREATION_URL)
                        .content("{\"objectId\": \"12345\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-HubSpot-Signature-v3", signature))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should not allow timestamp older than five minutes")
    void testOldTimestamp() throws Exception {
        String body = "{\"objectId\": \"12345\"}";
        String method = "POST";
        String timestamp = String.valueOf(System.currentTimeMillis() - (30 * 6 * 1000));
        String signature = generateSignature(method, CONTACT_CREATION_URL, body, timestamp, secret);

        mockMvc.perform(post(CONTACT_CREATION_URL)
                        .content("{\"objectId\": \"12345\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-HubSpot-Signature-v3", signature))
                .andExpect(status().isUnauthorized());
    }
}
