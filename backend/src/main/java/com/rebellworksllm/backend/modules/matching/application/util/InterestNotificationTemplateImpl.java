package com.rebellworksllm.backend.modules.matching.application.util;

import com.rebellworksllm.backend.modules.matching.application.StudentInterestHandlerServiceImpl;
import com.rebellworksllm.backend.modules.matching.application.TemplateService;
import com.rebellworksllm.backend.modules.matching.application.exception.TemplateException;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("templateThree")
public class InterestNotificationTemplateImpl implements TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(StudentInterestHandlerServiceImpl.class);
    @Override
    public List<String> generateVacancyTemplateParams(String candidateName, Vacancy vac1, Vacancy vac2, Vacancy vac3, Vacancy vac4, ContactResponseMessage responseMessage) {

        String vacatureTitel = vac1.title();

        if (responseMessage.message().endsWith("2")) {
            vacatureTitel = vac2.title();
        } else if(responseMessage.message().endsWith("3")){
            vacatureTitel = vac3.title();
        } else if (responseMessage.message().endsWith("4")){
            vacatureTitel = vac4.title();
        }

        try {
            List<String> templateContent = List.of(
                    vacatureTitel
            );
            return templateContent;
        } catch (Exception ex) {
            logger.error("Error generating vacancy template: {}", ex.getMessage(), ex);
            throw new TemplateException("Error generating vacancy template: " + ex.getMessage(), ex);
        }
    }
}
