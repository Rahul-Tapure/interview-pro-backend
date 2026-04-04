package com.interviewpro.interviewpro.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Judge0Response {

    private String stdout;
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private Judge0Status status;

    private Double time;
    private Integer memory;

    private String message;
}
