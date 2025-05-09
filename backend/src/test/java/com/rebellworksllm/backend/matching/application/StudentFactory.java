package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.embedding.domain.Vectors;
import com.rebellworksllm.backend.matching.domain.Student;

public class StudentFactory {

    public static Student createStudent(Vectors vectors) {
        return new Student(
                "John Doe",
                "john.doe@domain.com",
                "12345678",
                "study",
                "looking for...",
                "Java",
                vectors
        );
    }
}
