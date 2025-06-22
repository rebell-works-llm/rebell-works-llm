package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.application.exception.TemplateException;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("templateTwo")
public class ExtraVacancyTemplateServiceImpl implements TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac3, Vacancy vac4) {

        try {
            List<String> templateContent = List.of(
                    check(vac3.title()), check(vac3.description()), check(vac3.workingHours()),
                    check(vac3.salary()), check(vac3.function()),
                    check(vac4.title()), check(vac4.description()), check(vac4.workingHours()),
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
