package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.SupabaseResponse;
import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.application.util.ScoreService;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Qualifier("pineconeSupabaseMatchEngine")
public class PineconeSupabaseMatchEngine implements MatchEngine {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private static final int TOP_K = 50;

    private final PineconeService pineconeService;
    private final SupabaseService supabaseService;

    public PineconeSupabaseMatchEngine(PineconeService pineconeService, SupabaseService supabaseService) {
        this.pineconeService = pineconeService;
        this.supabaseService = supabaseService;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {
        logger.info("Starting Pinecone match engine for student: {}, topK: {}", student.name(), TOP_K);
        try {
            List<StudentVacancyMatch> initialMatches = pineconeService.queryTopMatches(student, TOP_K);
            return initialMatches.stream()
                    .map(match -> {
                        SupabaseResponse supabaseVacancy = supabaseService.getVacancyById(match.vacancy().id());
                        double newScore = ScoreService.priorityScore(match.matchScore(), supabaseVacancy.priority(), supabaseVacancy.matchCount());

                        return new StudentVacancyMatch(
                                match.vacancy(),
                                match.student(),
                                newScore
                        );
                    })
                    .sorted(Comparator.comparingDouble(StudentVacancyMatch::matchScore).reversed())
                    .limit(amount)
                    .toList();

        } catch (Exception e) {
            logger.error("Failed to query matches from Pinecone for student: {}, error: {}", student.name(), e.getMessage(), e);
            throw new MatchingException("Pinecone matching failed", e);
        }
    }
}
