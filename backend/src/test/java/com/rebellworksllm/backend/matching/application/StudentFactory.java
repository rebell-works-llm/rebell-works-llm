package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;
import com.rebellworksllm.backend.modules.matching.domain.Student;

public class StudentFactory {

    public static Student createStudent(EmbeddingResult embeddingResult) {
        return new Student(
                "1",
                "John Doe",
                "john.doe@domain.com",
                "12345678",
                "study",
                "looking for...",
                "Java",
                embeddingResult
        );
    }
}
