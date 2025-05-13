package com.rebellworksllm.backend.embedding.application;

import com.rebellworksllm.backend.embedding.domain.Vectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OpenAITextEmbedderIntegrationTest {

    @Autowired
    private OpenAITextEmbedder embedder;

    @Test
    void testTextEmbedder() {
        String text = "Hello World";
        Vectors vectors = embedder.embedText(text);

        assertEquals(1536, vectors.embeddings().size());
    }
}
