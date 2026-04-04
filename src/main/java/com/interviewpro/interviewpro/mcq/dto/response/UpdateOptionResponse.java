package com.interviewpro.interviewpro.mcq.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateOptionResponse {
    private Long optionId; // null = new option
    private String optionText;
    @JsonProperty("isCorrect")
    private boolean isCorrect;
}
