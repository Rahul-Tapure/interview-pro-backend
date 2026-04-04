package com.interviewpro.interviewpro.coding.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodingTestListResponse {

    private Long testId;          // ✅ SAME as MCQ
    private String title;
    private Integer durationMinutes;

    private Boolean publicTest;
    private String testType;
}
