package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("prod")
public class PineconeSupabaseMatchEngine implements MatchEngine {

    private final PineconeService pineconeService;

    public PineconeSupabaseMatchEngine(PineconeService pineconeService) {
        this.pineconeService = pineconeService;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {

        try {
            return pineconeService.queryTopMatches(student, amount);
        } catch (Exception e) {
            throw new MatchingException("Pinecone matching failed", e);
        }
    }
}
