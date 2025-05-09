package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;

import java.util.List;

public interface StudentJobMatchingService {

    List<StudentVacancyMatch> findBestMatches(Student student, List<Vacancy> vacancies, int limit);
}
