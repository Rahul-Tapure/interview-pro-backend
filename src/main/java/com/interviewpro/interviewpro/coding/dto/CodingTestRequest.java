package com.interviewpro.interviewpro.coding.dto;

import com.interviewpro.interviewpro.mcq.enums.TestType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CodingTestRequest {
    private String title;
    private String description;
    private TestType testType;
    private int durationMinutes;
    private int totalQuestions;
    private boolean publicTest;
}
