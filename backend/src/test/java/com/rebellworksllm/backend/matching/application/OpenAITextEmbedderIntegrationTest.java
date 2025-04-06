package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.embedding.application.OpenAITextEmbedder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OpenAITextEmbedderIntegrationTest {

    @Autowired
    private OpenAITextEmbedder embedder;

    @Test
    void testTextEmbedder() {
        String text = "Hello World";
        List<Double> embeddings = embedder.embedText(text);

        assertEquals(1536, embeddings.size());
    }
}
