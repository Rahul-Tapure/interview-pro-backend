package com.interviewpro.interviewpro.coding.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CodingTestCaseRequest {

    private String input;
    private String expectedOutput;
    private Boolean sample;
}
