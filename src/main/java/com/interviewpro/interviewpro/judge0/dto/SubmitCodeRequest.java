package com.interviewpro.interviewpro.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubmitCodeRequest {

    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("sourceCode")
    private String sourceCode;

    @JsonProperty("languageId")
    private Integer languageId;

	public String attemptId;
}
