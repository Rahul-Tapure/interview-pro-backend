package com.interviewpro.interviewpro.mcq.dto.response;

import java.util.List;

import com.interviewpro.interviewpro.mcq.entity.McqQuestion;

import lombok.Data;

@Data
public class UpdateQuestionResponse {

    private Long questionId; // null = new question
    private String questionText;
    private McqQuestion.Difficulty difficulty;

    private List<UpdateOptionResponse> options;
}
