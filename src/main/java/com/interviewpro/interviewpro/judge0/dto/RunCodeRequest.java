package com.interviewpro.interviewpro.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RunCodeRequest {

    @JsonProperty("sourceCode")
    private String sourceCode;

    @JsonProperty("languageId")
    private Integer languageId;

    @JsonProperty("input")
    private String input;
}
