package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.application.exception.TemplateException;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("templateOne")
public class TemplateServiceImpl implements TemplateService {

    private final OpenAIVacancySummaryService openAIVacancySummaryService;

    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    public TemplateServiceImpl(OpenAIVacancySummaryService openAIVacancySummaryService) {
        this.openAIVacancySummaryService = openAIVacancySummaryService;
    }

    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2) {
        String candidateInfo = candidateName != null ? candidateName : "Unknown candidate";
        logger.info("Generating vacancy template for candidate: {}", candidateInfo);

        logger.debug("Job 1 description: {}", vac1.description());
        logger.debug("Job 2 description: {}", vac2.description());

        String vac1GeneratedDescription = openAIVacancySummaryService.generateSummary(vac1.description());
        String vac2GeneratedDescription = openAIVacancySummaryService.generateSummary(vac2.description());
        logger.debug("Vacancy 1 generated description: {}", vac1GeneratedDescription);
        logger.debug("Vacancy 2 generated description: {}", vac2GeneratedDescription);

        try {
            List<String> templateContent = List.of(
                    check(candidateName),
                    check(vac1.title()), check(vac1GeneratedDescription), check(vac1.workingHours()),
                    check(vac1.salary()), check(vac1.function()),
                    check(vac2.title()), check(vac2GeneratedDescription), check(vac2.workingHours()),
                    check(vac2.salary()), check(vac2.function())
            );

            logger.info("Successfully generated vacancy template for candidate: {}", candidateInfo);
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
