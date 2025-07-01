package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.common.utils.TextUtils;
import com.rebellworksllm.backend.modules.openai.application.OpenAICompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpenAIVacancySummaryServiceImpl implements OpenAIVacancySummaryService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private final OpenAICompletionService chatService;

    public OpenAIVacancySummaryServiceImpl(OpenAICompletionService chatService) {
        this.chatService = chatService;
    }

    @Override
    public String generateSummary(String vacancyDescription) {
        if (vacancyDescription == null) {
            throw new IllegalArgumentException("Vacancy description cannot be null");
        }

        logger.info("Generating summary for vacancy with description length: {}", vacancyDescription.length());

        String sanitizedDescription = TextUtils.sanitize(vacancyDescription);
        logger.debug("Sanitized description: {}", sanitizedDescription);
        String prompt = buildPrompt(sanitizedDescription);
        logger.debug("Prompt: {}", prompt);
        String summary = chatService.complete(Map.of("system", "You are a helpful assistant.", "user", prompt));
        logger.debug("AI generated summary: {}", summary);

        String sanitizedSummary = TextUtils.sanitize(summary);
        logger.debug("Sanitized AI summary: {}", sanitizedSummary);

        String cappedSummary = TextUtils.capLength(sanitizedSummary, MAX_DESCRIPTION_LENGTH);
        logger.debug("Capped summary: {}", cappedSummary);

        return validateLength(cappedSummary);
    }

    private String buildPrompt(String description) {
        return """
                **ROL**
                Je bent een ervaren vacaturetekst-schrijver voor WhatsApp-templates. Je taak is om lange vacatureteksten samen te vatten tot een duidelijke, vriendelijke en uitnodigende alinea die studenten aanspreekt. De samenvatting moet direct bruikbaar zijn in WhatsApp-berichten en mag maximaal %d tekens bevatten.

                **RESTRICTIES**
                - Gebruik uitsluitend standaard Latijns schrift (geen emoji's, symbolen, accenten of niet-Latijnse tekens).
                - Vermeld géén salaris of locatie (deze worden apart toegevoegd).
                - Gebruik geen opsommingstekens, lijsten of opmaak; schrijf één goedlopende alinea.
                - Beantwoord altijd in dezelfde taal als de originele vacaturetekst.

                **DOEL**
                - Zorg voor een warme, toegankelijke en wervende toon die relevant is voor studenten of starters.
                - Behoud concrete verantwoordelijkheden, technologieën en bijzonderheden uit de originele tekst.
                - Benoem alle expliciet genoemde hoofdtaken en verantwoordelijkheden uit de tekst, tenzij door de lengtebeperking echt niet alles past.
                - Licht specifieke technologieën, platforms of methoden uit als ze genoemd worden.
                - Vermijd herhaling of loze opvulling.

                **OUTPUT**
                Geef alleen de samenvatting als platte tekst (één alinea, maximaal %d tekens).

                **VACATURETEKST**
                %s
                """.formatted(MAX_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH, description);
    }

    private String validateLength(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (text.length() > MAX_DESCRIPTION_LENGTH) {
            logger.debug("Trimming summary from {} to {}", text.length(), MAX_DESCRIPTION_LENGTH);
            return text.substring(0, (MAX_DESCRIPTION_LENGTH - 1)) + "...";
        }
        return text;
    }
}
