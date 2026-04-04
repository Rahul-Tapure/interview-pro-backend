package com.interviewpro.interviewpro.mcq.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FullTestEditResponse {

    private Long testId;
    private String title;
    private int durationMinutes;

    private List<QuestionWithAnswerResponse> questions;
}

