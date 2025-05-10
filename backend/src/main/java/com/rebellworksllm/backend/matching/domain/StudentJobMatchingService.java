package com.rebellworksllm.backend.matching.domain;

import java.util.List;

public interface StudentJobMatchingService {

    List<StudentVacancyMatch> findBestMatches(Student student, List<Vacancy> vacancies, int limit);
}
