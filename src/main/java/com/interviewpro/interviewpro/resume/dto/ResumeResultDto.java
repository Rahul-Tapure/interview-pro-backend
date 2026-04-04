package com.interviewpro.interviewpro.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResultDto {
    private int score;
    private int atsOptimizationScore;
    private List<String> pros;
    private List<String> cons;
    private List<String> suggestions;
}