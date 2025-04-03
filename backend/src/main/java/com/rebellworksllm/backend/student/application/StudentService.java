package com.rebellworksllm.backend.student.application;

public interface StudentService {

    StudentDto findByPhoneNumber(String phoneNumber);
}
