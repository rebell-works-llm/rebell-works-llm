package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("pineconeSupabaseMatchEngine")
public class PineconeSupabaseMatchEngine implements MatchEngine {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);


    private final PineconeService pineconeService;

    public PineconeSupabaseMatchEngine(PineconeService pineconeService) {
        this.pineconeService = pineconeService;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {
        logger.info("Starting Pinecone match engine for student: {}, topK: {}", student.name(), amount);
        try {
            return pineconeService.queryTopMatches(student, amount);
        } catch (Exception e) {
            logger.error("Failed to query matches from Pinecone for student: {}, error: {}", student.name(), e.getMessage(), e);
            throw new MatchingException("Pinecone matching failed", e);
        }
    }
}
