package com.interviewpro.interviewpro.mcq.dto.request;

import lombok.Data;
import java.util.List;

import com.interviewpro.interviewpro.mcq.dto.response.UpdateQuestionResponse;

@Data
public class UpdateFullTestRequest {

    private String title;
    private int durationMinutes;

    private List<UpdateQuestionResponse> questions;
}
