package com.interviewpro.interviewpro.coding.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CodingTestResponse {
    private Long testId;
    private String title;
    private String description;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private boolean publicTest;
    private String testType;
}
