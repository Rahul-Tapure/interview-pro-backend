package com.interviewpro.interviewpro.judge0.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.interviewpro.interviewpro.judge0.dto.*;
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.judge0.service.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/interviewpro/coding")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class CodingController {

    private final CodingRunService runService;
    private final CodingSubmitService submitService;
    private final MainService mainService;
    
    @PostMapping("/run")
    public Judge0Response run(@RequestBody RunCodeRequest request)
    {
    	return runService.run(request);
    }

    @PostMapping("/submit")
    public SubmitResultResponse submit(@RequestBody SubmitCodeRequest request) {
        return submitService.submit(request);
    }
    
    @GetMapping("/languages")
    public LanguagesResponse[] languages() {
    	return mainService.languages();
    }


}
