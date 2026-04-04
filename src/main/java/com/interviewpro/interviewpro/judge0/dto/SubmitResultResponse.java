package com.interviewpro.interviewpro.judge0.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitResultResponse {

    private Integer passed;
    private Integer total;
    private Integer score;
    private String status;
    private String message;
}
