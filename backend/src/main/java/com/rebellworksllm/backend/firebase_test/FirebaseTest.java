package com.rebellworksllm.backend.firebase_test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.rebellworksllm.backend.firebase_test.presentation.StudentResponseDto;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirebaseTest {

    private static void initFirebase() throws IOException {
        InputStream serviceAccount = new FileInputStream("backend/src/main/resources/rebell-works-llm-firebase-adminsdk-fbsvc-575eea3fcf.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        initFirebase();

        Firestore db = FirestoreClient.getFirestore();

        List<QueryDocumentSnapshot> documents = db.collection("students")
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot doc : documents) {
            StudentResponseDto studentResponseDto = new StudentResponseDto(doc.getString("name"), doc.getString("email"));
            System.out.println(studentResponseDto);
        }
    }
}
