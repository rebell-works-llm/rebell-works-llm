package com.rebellworksllm.backend.matching.application;

import java.util.List;

public interface VacancyMatchingService {

    String findBestMatch(float[] studentVector, List<float[]> vacancyVectors, List<String> titles);
}
