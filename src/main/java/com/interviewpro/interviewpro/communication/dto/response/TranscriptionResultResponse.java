package com.interviewpro.interviewpro.communication.dto.response;

import java.math.BigDecimal;

import com.interviewpro.interviewpro.communication.enums.TranscriptionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranscriptionResultResponse {
    private String assemblyaiJobId;
    private String transcript;
    private BigDecimal confidenceScore;
    private Integer audioDurationSeconds;
    private TranscriptionStatus status;
}
