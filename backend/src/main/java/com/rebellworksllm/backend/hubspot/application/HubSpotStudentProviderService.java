package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.hubspot.data.HubSpotStudentService;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import org.springframework.stereotype.Service;

@Service
public class HubSpotStudentProviderService implements HubSpotStudentProvider {

    private final HubSpotStudentService hubSpotStudentService;

    public HubSpotStudentProviderService(HubSpotStudentService hubSpotStudentService) {
        this.hubSpotStudentService = hubSpotStudentService;
    }

    @Override
    public StudentContact getStudentById(long id) {
        return hubSpotStudentService.getStudentById(id);
    }

    @Override
    public StudentContact getStudentByPhone(String phone) {
        return hubSpotStudentService.getStudentByPhone(phone);
    }
}
