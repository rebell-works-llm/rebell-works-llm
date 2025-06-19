package com.rebellworksllm.backend.hubspot.data;

import java.util.List;

public record StudentSearchResponse(

        String total,
        List<StudentRequest> results
) {
}
