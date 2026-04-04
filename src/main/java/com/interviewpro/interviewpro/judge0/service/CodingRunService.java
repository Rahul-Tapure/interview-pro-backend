package com.interviewpro.interviewpro.judge0.service;

import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.judge0.dto.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodingRunService {

    private final Judge0Service judge0Service;

    public Judge0Response run(RunCodeRequest request) {
        return judge0Service.execute(
                request.getSourceCode(),
                request.getLanguageId(),
                request.getInput()
        );
    }

}
