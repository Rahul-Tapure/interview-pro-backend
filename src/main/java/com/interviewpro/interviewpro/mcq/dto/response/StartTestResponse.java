package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StartTestResponse {

	private String testName;
    private Long attemptId;
    private Long testId;
    private int durationMinutes;
    private List<QuestionResponse> questions;
}
