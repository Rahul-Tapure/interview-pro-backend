package com.interviewpro.interviewpro.mcq.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddOptionRequest {

    @NotBlank
    private String optionText;

    @JsonProperty("isCorrect")   // ✅ FORCE JSON BINDING
    private boolean isCorrect;
}
