package com.rebellworksllm.backend.vacancies.application;

import com.rebellworksllm.backend.vacancies.data.PineconeService;
import com.rebellworksllm.backend.vacancies.domain.ScoredVacancy;
import com.rebellworksllm.backend.vacancies.domain.Vacancy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacancyService implements VacancyProvider {

    private final PineconeService pineconeService;

    public VacancyService(PineconeService pineconeService) {
        this.pineconeService = pineconeService;
    }

    @Override
    public List<ScoredVacancy> getVacanciesBySimilarity(List<Double> vector, int topK) {
        return pineconeService.queryTopMatches(vector, topK);
    }

    @Override
    public Vacancy getVacancyById(int id) {
        // TODO: return supabase vacancy from SupabaseService
        return null;
    }

    @Override
    public List<Vacancy> getVacanciesByIds(List<Integer> ids) {
        // TODO: return supabases vacancies from SupabaseService

        return List.of();
    }
}
