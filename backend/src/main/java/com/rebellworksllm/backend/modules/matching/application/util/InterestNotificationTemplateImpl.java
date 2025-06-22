package com.rebellworksllm.backend.modules.matching.application.util;

import com.rebellworksllm.backend.modules.matching.application.TemplateService;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;

import java.util.List;

public class InterestNotificationTemplateImpl implements TemplateService {
    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2) {
        return null;
    }
}
