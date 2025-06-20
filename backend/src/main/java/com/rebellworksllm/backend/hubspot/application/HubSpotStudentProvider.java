package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;

public interface HubSpotStudentProvider {

    StudentContact getStudentById(long id);

    StudentContact getStudentByPhone(String phone);
}
