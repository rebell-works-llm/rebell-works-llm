package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.matching.domain.Student;

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
