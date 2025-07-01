package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;

import java.util.List;

public interface TemplateService {
    List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2, Vacancy vac3, Vacancy vac4, ContactResponseMessage responseMessage);

}
