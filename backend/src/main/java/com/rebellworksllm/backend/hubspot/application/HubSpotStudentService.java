package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.hubspot.presentation.dto.StudentContact;

public interface HubSpotStudentService {

    StudentContact getStudentById(long id);
}
