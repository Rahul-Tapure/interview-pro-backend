package com.interviewpro.interviewpro.mcq.dto.request;
import com.interviewpro.interviewpro.mcq.entity.McqQuestion.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddQuestionRequest {

    @NotBlank
    private String questionText;

    @NotNull
    private Difficulty difficulty;
}
