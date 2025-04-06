package com.rebellworksllm.backend.matching.domain;

public class Vacancy {

    private String title;
    private float[] vectors;

    public Vacancy(String title, float[] vectors) {
        this.title = title;
        this.vectors = vectors;
    }

    public String getTitle() {
        return title;
    }

    public float[] getVectors() {
        return vectors;
    }
}
