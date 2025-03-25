package com.rebellworksllm.backend.firebase_test.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.rebellworksllm.backend.firebase_test.presentation.StudentResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final Firestore db;

    public StudentService(Firestore db) {
        this.db = db;
    }

    public List<StudentResponseDto> getAllStudents() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = db.collection("students")
                .get()
                .get()
                .getDocuments();
        return documents.stream()
                .map(doc -> new StudentResponseDto(
                        doc.getString("name"),
                        doc.getString("email")
                ))
                .collect(Collectors.toList());
    }
}
