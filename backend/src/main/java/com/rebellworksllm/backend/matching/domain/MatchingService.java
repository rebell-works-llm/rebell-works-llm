package com.rebellworksllm.backend.matching.domain;

import java.util.List;

public interface MatchingService {

    List<StudentVacancyMatch> findBestMatches(float[] studentQueryVector, List<Vacancy> vacancies, int numOfResults);
}
