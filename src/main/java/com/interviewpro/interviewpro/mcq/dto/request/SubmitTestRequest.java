package com.interviewpro.interviewpro.mcq.dto.request;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitTestRequest {
    // questionId -> selectedOptionId
    private Map<Long, Long> answers;
}
