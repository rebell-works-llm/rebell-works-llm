package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;

import java.util.List;

public interface MatchEngine {

    List<StudentVacancyMatch> query(Student student, int amount);
}
