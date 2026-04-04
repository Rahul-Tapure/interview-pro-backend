package com.interviewpro.interviewpro.communication.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class FeedbackResponse {
    private Long id;
    private BigDecimal aiScore;
    private BigDecimal grammarScore;
    private BigDecimal clarityScore;
    private BigDecimal fluencyScore;
    private String strengths;
    private String weaknesses;
    private String suggestions;
    private LocalDateTime createdAt;
}
