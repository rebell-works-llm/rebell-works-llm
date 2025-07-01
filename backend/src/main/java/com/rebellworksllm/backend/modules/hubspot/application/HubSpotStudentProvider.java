package com.rebellworksllm.backend.modules.hubspot.application;

import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;

public interface HubSpotStudentProvider {

    StudentContact getStudentById(long id);

    StudentContact getStudentByPhone(String phone);
}
