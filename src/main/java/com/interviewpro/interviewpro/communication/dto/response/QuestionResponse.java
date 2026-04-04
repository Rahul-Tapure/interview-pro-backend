package com.interviewpro.interviewpro.communication.dto.response;

import com.interviewpro.interviewpro.communication.enums.DifficultyLevel;
import com.interviewpro.interviewpro.communication.enums.QuestionCategory;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private Integer timeLimit;
    private Integer questionOrder;
    private DifficultyLevel difficultyLevel;
    private QuestionCategory category;
}
