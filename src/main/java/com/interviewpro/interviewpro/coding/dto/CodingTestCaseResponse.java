package com.interviewpro.interviewpro.coding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class CodingTestCaseResponse {

    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean sample;
}
