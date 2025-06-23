package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.application.exception.TemplateException;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("templateTwo")
public class ExtraVacancyTemplateServiceImpl implements TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private final OpenAIVacancySummaryService openAIVacancySummaryService;

    public ExtraVacancyTemplateServiceImpl(OpenAIVacancySummaryService openAIVacancySummaryService) {
        this.openAIVacancySummaryService = openAIVacancySummaryService;
    }

    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac3, Vacancy vac4, Vacancy vac5, Vacancy vac6, ContactResponseMessage responseMessage) {

        logger.debug("Job 3 description: {}", vac3.description());
        logger.debug("Job 4 description: {}", vac4.description());

        String vac3GeneratedDescription = openAIVacancySummaryService.generateSummary(vac3.description());
        String vac4GeneratedDescription = openAIVacancySummaryService.generateSummary(vac4.description());

        logger.debug("Vacancy 3 generated description: {}", vac3GeneratedDescription);
        logger.debug("Vacancy 4 generated description: {}", vac4GeneratedDescription);

        try {
            List<String> templateContent = List.of(
                    check(vac3.title()), check(vac3GeneratedDescription), check(vac3.workingHours()),
                    check(vac3.salary()), check(vac3.function()),
                    check(vac4.title()), check(vac4GeneratedDescription), check(vac4.workingHours()),
                    check(vac4.salary()), check(vac4.function())
            );
            return templateContent;
        } catch (Exception ex) {
            logger.error("Error generating vacancy template: {}", ex.getMessage(), ex);
            throw new TemplateException("Error generating vacancy template: " + ex.getMessage(), ex);
        }
    }

    private String check(String input) {
        return (input == null || input.trim().isEmpty())
                ? "Unknown"
                : input.trim();
    }
}
