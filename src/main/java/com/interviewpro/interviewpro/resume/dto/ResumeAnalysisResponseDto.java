package com.interviewpro.interviewpro.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ResumeAnalysisResponseDto {


	    private Long id;

	    private String role;   // ✅ add this

	    private int score;
	    private int atsOptimizationScore;

	    private List<String> pros;
	    private List<String> cons;
	    private List<String> suggestions;

	
}