package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuestionResponse {
    private Long questionId;
    private String questionText;
    private List<OptionResponse> options;
}
