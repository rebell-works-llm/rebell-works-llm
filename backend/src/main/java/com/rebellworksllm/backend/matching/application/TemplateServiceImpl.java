package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.TemplateException;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2) {
        String candidateInfo = candidateName != null ? candidateName : "Unknown candidate";
        logger.info("Generating vacancy template for candidate: {}", candidateInfo);

        try {
            List<String> templateContent = List.of(
                    check(candidateName),
                    check(vac1.title()), check(vac1.description()), check(vac1.workingHours()),
                    check(vac1.salary()), check(vac1.function()),
                    check(vac2.title()), check(vac2.description()), check(vac2.workingHours()),
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
