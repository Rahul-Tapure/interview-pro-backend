package com.interviewpro.interviewpro.coding.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CodingQuestionRequest {

    private String title;
    private String description;
    private String difficulty;
    private String constraints;
    private Integer timeLimit;
    private Integer memoryLimit;
}
