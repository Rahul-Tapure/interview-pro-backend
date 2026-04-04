package com.interviewpro.interviewpro.mcq.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousQuestionResponse {

    private Long questionId;
    private String questionText;

    // enum converted to string for frontend
    private String difficulty;

    private List<PreviousOptionResponse> options;
}
