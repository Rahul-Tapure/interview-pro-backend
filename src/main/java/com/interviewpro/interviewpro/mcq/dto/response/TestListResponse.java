package com.interviewpro.interviewpro.mcq.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interviewpro.interviewpro.mcq.enums.TestType;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TestListResponse {

    private Long testId;
    private String title;
    private Integer durationMinutes;

    private Boolean publicTest;   // ✅ GOOD NAME
    private String testType;
    private int totalQuestions;
}
