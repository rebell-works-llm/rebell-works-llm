package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.CannotFetchVacancyEmbeddingsException;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import com.rebellworksllm.backend.matching.domain.VacancyService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DummyVacancyService implements VacancyService {

    private static final String JSON_FILE_PATH = "vector-vacancies.json";

    @Override
    public List<Vacancy> getVacancies() {
        List<Vacancy> vacancies = new ArrayList<>();
        for (Object object : readJsonFile()) {
            JSONObject vacancy = (JSONObject) object;

            JSONArray embeddingArray = (JSONArray) vacancy.get("embedding");
            List<Double> vector = new ArrayList<>();
            for (int i = 0; i < embeddingArray.size(); i++) {
                vector.set(i, Double.parseDouble(embeddingArray.get(i).toString()));
            }

            vacancies.add(new Vacancy(vacancy.get("title").toString(), vector));
        }

        return vacancies;
    }

    private JSONArray readJsonFile() {
        try {
            Object obj = new JSONParser().parse(new FileReader(JSON_FILE_PATH));
            JSONObject jsonObject = (JSONObject) obj;

            return (JSONArray) jsonObject.get("dummy-vacancies");
        } catch (IOException | ParseException e) {
            throw new CannotFetchVacancyEmbeddingsException("Cannot fetch vacancy embeddings from file'"
                    + DummyVacancyService.JSON_FILE_PATH + "': " + e.getMessage());
        }
    }
}
