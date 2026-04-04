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
public class QuestionWithAnswerResponse {
    private Long questionId;
    private String questionText;
    private List<OptionWithAnswerResponse> options;
}

