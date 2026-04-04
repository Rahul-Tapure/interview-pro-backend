package com.interviewpro.interviewpro.mcq.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestViewResponse {
    private Long testId;
    private String title;
    private boolean attempted;

    private List<QuestionWithAnswerResponse> questions;
}
