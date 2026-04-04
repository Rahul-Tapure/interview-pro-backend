package com.interviewpro.interviewpro.communication.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CreateTestRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Total questions required")
    private Integer totalQuestions;

    @NotNull(message = "Duration required")
    private Integer durationMinutes;

    private String createdBy;
}