package com.rebellworksllm.backend.matching.data;

import com.rebellworksllm.backend.matching.data.dto.MatchMessageRequest;
import com.rebellworksllm.backend.matching.data.dto.MatchMessageResponse;

public interface MatchMessageRepository {

    MatchMessageResponse save(MatchMessageRequest request);

    MatchMessageResponse findByContactPhone(String phone);
}
