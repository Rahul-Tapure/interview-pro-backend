package com.interviewpro.interviewpro.communication.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.interviewpro.interviewpro.communication.enums.SubmissionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponse {
    private Long id;
    private Long testId;
    private Long submissionId;
 // was: private Long userId;
    private String userId;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private BigDecimal overallScore;
    private SubmissionStatus status;
    private List<AnswerResponse> answers;
}

