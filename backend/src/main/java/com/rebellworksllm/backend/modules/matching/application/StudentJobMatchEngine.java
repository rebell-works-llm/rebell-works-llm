package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.domain.Student;
import com.rebellworksllm.backend.modules.matching.domain.StudentVacancyMatch;

import java.util.List;

public interface StudentJobMatchEngine {

    List<StudentVacancyMatch> query(Student student, int amount);
}
