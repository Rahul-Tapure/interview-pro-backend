package com.interviewpro.interviewpro.communication.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.interviewpro.interviewpro.communication.enums.TranscriptionStatus;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AnswerResponse {
    private Long id;
    private Long questionId;
    private String audioUrl;
    private String assemblyaiJobId;
    private TranscriptionStatus transcriptionStatus;
    private String transcript;
    private BigDecimal confidenceScore;
    private Integer audioDurationSeconds;
    private LocalDateTime createdAt;
    private FeedbackResponse feedback;
}

