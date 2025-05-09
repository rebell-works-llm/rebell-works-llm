package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.StudentDto;

public interface StudentService {

    StudentDto getStudentById(long id);
}
