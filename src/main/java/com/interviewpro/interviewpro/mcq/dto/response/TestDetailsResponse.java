package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TestDetailsResponse {
    private Long testId;
    private String title;
    private int totalQuestions;
    private int durationMinutes;
	   
}
