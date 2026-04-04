package com.interviewpro.interviewpro.communication.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.interviewpro.interviewpro.communication.enums.TestStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestResponse {
    private Long id;
    private String title;
    private String description;
    private Integer totalQuestions;
    private TestStatus status;
    private LocalDateTime createdAt;
    private List<QuestionResponse> questions;
}
