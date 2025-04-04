package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.VacancyMatchDto;
import com.rebellworksllm.backend.matching.application.util.JSONFileReader;
import com.rebellworksllm.backend.matching.presentation.VacancyQueryDto;
import com.rebellworksllm.backend.student.application.StudentService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DummyQueryService implements VacancyQueryService {

    private final StudentService studentService;
    private final VacancyQueryEmbedder vacancyQueryEmbedder;
    private final VacancyMatchingService vacancyMatchingService;

    public DummyQueryService(StudentService studentService, VacancyQueryEmbedder vacancyQueryEmbedder, VacancyMatchingService vacancyMatchingService) {
        this.studentService = studentService;
        this.vacancyQueryEmbedder = vacancyQueryEmbedder;
        this.vacancyMatchingService = vacancyMatchingService;
    }

    @Override
    public VacancyMatchDto processQuery(VacancyQueryDto request) {
        String studentPhoneNumber = request.phoneNumber();
        String studentMessageText = request.messageText();

        System.out.println("Simulated WhatsApp Message from " + studentPhoneNumber + ": " + studentMessageText);

        // Fetch the student or throw if not found
//        StudentDto studentDto = studentService.findByPhoneNumber(studentPhoneNumber);

        // Embedding the student's job search to vector
        float[] embeddedQuery = vacancyQueryEmbedder.embedQueryToVector(studentMessageText);

        // TODO: Fetch Vacancy Data as Vector(s)
        JSONObject jsonObject = JSONFileReader.readJSONFile("vector-vacancies.json");
        JSONArray vacanciesArray = (JSONArray) jsonObject.get("dummy-vacancies");

        List<float[]> vacancyVectors = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        for (Object obj : vacanciesArray) {
            JSONObject vacancy = (JSONObject) obj;

            titles.add((String) vacancy.get("title"));

            JSONArray embeddingArray = (JSONArray) vacancy.get("embedding");
            float[] vector = new float[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                vector[i] = ((Number) embeddingArray.get(i)).floatValue();
            }
            vacancyVectors.add(vector);
        }

        // TODO: Match Student With Vacancies (GPT)
        String bestMatch = vacancyMatchingService.findBestMatch(embeddedQuery, vacancyVectors, titles);

        // TODO: Construct a fancy response message to send the best matches to the student
//        String mathResult = "Great to hear " + studentDto.name() + "!. I found a match for you: Junior Software Developer at the Hogeschool Utrecht, starting april 3.\n\nDoes this sound good to you?";

        // TODO: Return error if no matches found
        return VacancyMatchDto.fromVacancy("Nice! Here's a great opportunity for you: " + bestMatch);
    }
}
