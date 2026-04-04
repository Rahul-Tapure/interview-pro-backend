package com.interviewpro.interviewpro.communication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotNull(message = "Submission ID is required")
    private Long submissionId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Audio URL is required")
    private String audioUrl;

    @NotBlank(message = "AssemblyAI job ID is required")
    private String assemblyaiJobId;
}