package com.rebellworksllm.backend.matching.application.mapper;

import com.rebellworksllm.backend.matching.application.dto.ContactResponse;
import com.rebellworksllm.backend.matching.application.dto.StudentDto;

import java.util.Map;

public class ContactMapper {

    public static StudentDto toStudentDto(ContactResponse response) {
        Map<String, String> props = response.properties();

        return new StudentDto(
                props.get("firstname"),
                props.get("email"),
                props.get("phone"),
                props.get("studie"),
                props.get("op_zoek_naar_"),
                props.get("location"),
                props.get("geboortedatum")
        );
    }
}
