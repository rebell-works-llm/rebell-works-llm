package com.rebellworksllm.backend.openai.application;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.openai.domain.OpenAIEmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OpenAIEmbedderImplIntegrationTest {

    @Autowired
    private OpenAIEmbeddingService openAIEmbeddingService;

    @Test
    void testTextEmbedder() {
        String text = "Hello World";
        EmbeddingResult embeddingResult = openAIEmbeddingService.embedText(text);

        assertEquals(1536, embeddingResult.embeddings().size());
    }
}
