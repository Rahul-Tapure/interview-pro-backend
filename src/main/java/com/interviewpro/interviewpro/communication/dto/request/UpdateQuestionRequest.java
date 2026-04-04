package com.interviewpro.interviewpro.communication.dto.request;

import com.interviewpro.interviewpro.communication.enums.DifficultyLevel;
import com.interviewpro.interviewpro.communication.enums.QuestionCategory;

import lombok.Data;

@Data
public class UpdateQuestionRequest {

    private String questionText;

    private Integer timeLimit;

    private DifficultyLevel difficultyLevel;

    private QuestionCategory category;
}