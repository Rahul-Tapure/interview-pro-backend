package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionWithAnswerResponse {
    private Long optionId;
    private String optionText;
    private boolean isCorrect;
}
