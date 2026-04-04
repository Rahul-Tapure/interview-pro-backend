package com.interviewpro.interviewpro.coding.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CodingQuestionResponse {

    private Long questionId;
    private String title;
    private String difficulty;
    private String description;
    private String constraints;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Boolean published;
    private String createdBy;

    private Integer questionIndex;
    private List<CodingTestCaseResponse> testCases;
}
