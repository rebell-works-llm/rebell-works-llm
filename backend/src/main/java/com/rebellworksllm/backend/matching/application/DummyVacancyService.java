package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.matching.application.exception.CannotFetchVacancyEmbeddingsException;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class DummyVacancyService implements VacancyService {

    private static final String JSON_FILE_PATH = "vector-vacancies.json";

    @Override
    public List<Vacancy> getAllVacancies() {
        List<Vacancy> vacancies = new ArrayList<>();
        for (Object object : readJsonFile()) {
            JSONObject vacancyObject = (JSONObject) object;
            JSONArray embeddingArray = (JSONArray) vacancyObject.get("embedding");
            List<Double> vector = new ArrayList<>();

            for (Object o : embeddingArray) {
                vector.add(Double.parseDouble(o.toString()));
            }

            Vacancy vacancy = new Vacancy(
                    vacancyObject.get("id").toString(),
                    vacancyObject.get("title").toString(),
                    vacancyObject.get("website").toString(),
                    new EmbeddingResult(vector)
            );
            vacancies.add(vacancy);
        }

        return vacancies;
    }

    private JSONArray readJsonFile() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(JSON_FILE_PATH)) {
            if (inputStream == null) {
                throw new CannotFetchVacancyEmbeddingsException("File not found: " + JSON_FILE_PATH);
            }

            InputStreamReader reader = new InputStreamReader(inputStream);
            Object obj = new JSONParser().parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            return (JSONArray) jsonObject.get("dummy-vacancies");
        } catch (IOException | ParseException e) {
            throw new CannotFetchVacancyEmbeddingsException("Cannot fetch vacancy embeddings from: " + JSON_FILE_PATH, e);
        }
    }
}
