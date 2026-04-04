package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TestResponse {
    private Long testId;
    private String title;
    private int durationMinutes;
    private List<QuestionResponse> questions;
}
