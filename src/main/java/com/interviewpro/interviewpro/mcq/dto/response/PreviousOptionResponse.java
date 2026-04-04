package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousOptionResponse {

    private Long optionId;

    private String optionText;

    private boolean isCorrect;
}
