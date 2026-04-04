package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionCountResponse {

    private int total;    // totalQuestions from test
    private int current;  // questions added so far
}
