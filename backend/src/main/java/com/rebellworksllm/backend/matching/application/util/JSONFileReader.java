package com.rebellworksllm.backend.matching.application.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONFileReader {

    public static JSONObject readJSONFile(String filename) {
        try {
            Object obj = new JSONParser().parse(new FileReader(filename));
            return (JSONObject) obj;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
