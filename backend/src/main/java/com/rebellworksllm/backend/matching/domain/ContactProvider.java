package com.rebellworksllm.backend.matching.domain;

import com.rebellworksllm.backend.matching.application.dto.StudentDto;

public interface ContactProvider {

    StudentDto getByContactId(long id);
}
