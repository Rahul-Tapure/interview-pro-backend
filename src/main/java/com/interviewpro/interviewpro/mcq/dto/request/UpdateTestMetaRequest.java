package com.interviewpro.interviewpro.mcq.dto.request;

import lombok.Data;

@Data
public class UpdateTestMetaRequest {
    private String title;
    private int durationMinutes;
}
