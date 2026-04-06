package com.interviewpro.interviewpro.coding.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.interviewpro.interviewpro.coding.dto.*;
import com.interviewpro.interviewpro.coding.entity.CodingTest;
import com.interviewpro.interviewpro.coding.entity.CodingQuestion;
import com.interviewpro.interviewpro.coding.entity.CodingTestCase;
import com.interviewpro.interviewpro.coding.service.CodingCreatorService;
import com.interviewpro.interviewpro.coding.service.CodingStudentService;
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/interviewpro/coding/v1")
@RequiredArgsConstructor
public class CodingStudentController {

    private final CodingStudentService service;

    @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
    @GetMapping("/my-results")
    public List<ResultResponse> myResults() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return service.getCodingResults(email);
    }
}