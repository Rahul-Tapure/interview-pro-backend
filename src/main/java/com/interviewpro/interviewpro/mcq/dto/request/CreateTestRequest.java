package com.interviewpro.interviewpro.mcq.dto.request;

import com.interviewpro.interviewpro.mcq.enums.TestType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTestRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TestType testType;

    @NotNull
    private Integer durationMinutes;

    @NotNull
    private Integer totalQuestions;

	public boolean isPublic;
}
