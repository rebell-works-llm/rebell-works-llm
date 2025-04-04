package com.rebellworksllm.backend.student.application;

import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseStudentService implements StudentService {

    private final Firestore db;

    public FirebaseStudentService(Firestore db) {
        this.db = db;
    }

    @Override
    public StudentDto findByPhoneNumber(String phoneNumber) {
        try {
            // Search for a student with the phone number
            CollectionReference students = db.collection("students");
            Query query = students.whereEqualTo("phoneNumber", phoneNumber).limit(1);
            QuerySnapshot snapshot = query.get().get();

            // Check if no student found
            if (snapshot.isEmpty()) {
                throw new RuntimeException("No student found with phone number " + phoneNumber);
            }

            // Retrieve fields
            DocumentSnapshot document = snapshot.getDocuments().getFirst();
            String name = document.getString("name");
            String retrievedPhoneNumber = document.getString("phoneNumber");

            // Validate fields
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Student name cannot be null or empty");
            }
            if (retrievedPhoneNumber == null || retrievedPhoneNumber.trim().isEmpty()) {
                throw new RuntimeException("Student phone number cannot be null or empty");
            }

            return new StudentDto(name, retrievedPhoneNumber);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to query Firestore", e);
        }
    }
}
