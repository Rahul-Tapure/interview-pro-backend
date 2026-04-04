package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionResponse {
    private Long optionId;
    private String optionText;
}
