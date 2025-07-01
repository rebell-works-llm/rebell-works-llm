package com.rebellworksllm.backend.modules.vacancies.application;

import com.rebellworksllm.backend.modules.vacancies.data.PineconeService;
import com.rebellworksllm.backend.modules.vacancies.data.SupabaseResponse;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.vacancies.domain.Vacancy;
import com.rebellworksllm.backend.modules.vacancies.application.dto.MatchedVacancy;
import com.rebellworksllm.backend.modules.vacancies.data.SupabaseService;
import com.rebellworksllm.backend.modules.vacancies.domain.VacancyBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacancyService implements VacancyProvider {

    private final PineconeService pineconeService;
    private final SupabaseService supabaseService;

    public VacancyService(PineconeService pineconeService, SupabaseService supabaseService) {
        this.pineconeService = pineconeService;
        this.supabaseService = supabaseService;
    }

    @Override
    public List<MatchedVacancy> getVacanciesBySimilarity(List<Double> vector, int topK) {
        return pineconeService.queryTopMatches(vector, topK);
    }

    @Override
    public VacancyResponseDto getVacancyById(String id) {
        SupabaseResponse response = supabaseService.getVacancyById(id);
        Vacancy vacancy = new VacancyBuilder(response.id())
                .title(response.title())
                .description(response.description())
                .salary(response.salary())
                .workingHours(response.workingHours())
                .function(response.function())
                .priority(response.priority())
                .matchCount(response.matchCount())
                .link(response.link())
                .build();

        return VacancyResponseDto.from(vacancy);
    }

    @Override
    public void incrementMatchCount(String id) {
        supabaseService.updateMatchCount(id);
    }
}
