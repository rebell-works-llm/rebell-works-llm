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
import java.util.Objects;

@Service
public class DummyVacancyService implements VacancyService {

    private static final String JSON_FILE_PATH = "vector-vacancies.json";

    @Override
    public List<Vacancy> getAllVacancies() {
        List<Vacancy> vacancies = new ArrayList<>();
        for (Object object : readJsonFile()) {
            JSONObject vacancy = (JSONObject) object;
            JSONArray embeddingArray = (JSONArray) vacancy.get("embedding");
            List<Double> vector = new ArrayList<>();

            for (Object o : embeddingArray) {
                vector.add(Double.parseDouble(o.toString()));
            }

            vacancies.add(new Vacancy(vacancy.get("title").toString(), vector));
        }

        return vacancies;
    }

    private JSONArray readJsonFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (FileReader reader = new FileReader(Objects.requireNonNull(classLoader.getResource(JSON_FILE_PATH)).getFile())) {
            Object obj = new JSONParser().parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            return (JSONArray) jsonObject.get("dummy-vacancies");
        } catch (IOException | ParseException | NullPointerException e) {
            throw new CannotFetchVacancyEmbeddingsException("Cannot fetch vacancy embeddings from file '"
                    + JSON_FILE_PATH + "': " + e.getMessage());
        }
    }
}
