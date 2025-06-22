package com.rebellworksllm.backend.modules.matching.data;

import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageRequest;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageResponse;

public interface MatchMessageRepository {

    MatchMessageResponse save(MatchMessageRequest request);

    MatchMessageResponse findByContactPhone(String phone);
}
