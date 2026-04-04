package com.interviewpro.interviewpro.mcq.dto.request;

import java.util.List;

import com.interviewpro.interviewpro.mcq.entity.McqQuestion.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateQuestionRequest {

    @NotBlank
    private String questionText;

    @NotNull
    private Difficulty difficulty;   // ✅ entity enum

    @NotEmpty
    private List<AddOptionRequest> options;
}
