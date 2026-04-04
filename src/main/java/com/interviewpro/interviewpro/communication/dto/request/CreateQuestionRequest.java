package com.interviewpro.interviewpro.communication.dto.request;

import com.interviewpro.interviewpro.communication.enums.DifficultyLevel;
import com.interviewpro.interviewpro.communication.enums.QuestionCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateQuestionRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    @Min(value = 10, message = "Time limit must be at least 10 seconds")
    private Integer timeLimit;

    private Integer questionOrder;

    private DifficultyLevel difficultyLevel;

    private QuestionCategory category;
}
