package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.hubspot.domain.StudentContact;

public interface HubSpotStudentService {

    StudentContact getStudentById(long id);
}
