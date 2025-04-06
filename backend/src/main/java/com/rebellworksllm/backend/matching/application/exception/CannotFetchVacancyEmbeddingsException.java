package com.rebellworksllm.backend.matching.application.exception;

public class CannotFetchVacancyEmbeddingsException extends RuntimeException {

    public CannotFetchVacancyEmbeddingsException(String message) {
        super(message);
    }
}
