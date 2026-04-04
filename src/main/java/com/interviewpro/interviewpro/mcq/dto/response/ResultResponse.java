package com.interviewpro.interviewpro.mcq.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultResponse {

    // ✅ NEW
    private String title;      // Test title
    private String type;       // APTITUDE / TECHNICAL

    private int score;

    private int totalQuestions;
    private int correct;
    private int wrong;
    private int unattempted;

    private String timeTaken;   // HH:mm:ss
    private boolean passed;

    private String message;
}
