package com.rebellworksllm.backend.modules.openai.application;

import java.util.Map;

public interface OpenAICompletionService {

    String complete(Map<String, String> messages);
}
