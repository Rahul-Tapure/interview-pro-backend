package com.interviewpro.interviewpro.coding.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.interviewpro.interviewpro.coding.service.CodingStudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interviewpro/coding")
public class CodingStudentController {

    private final CodingStudentService service;

    // ✅ Get My Results
    @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
    @GetMapping("/v1/my-results")
    public List<ResultResponse> myResults() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return service.getCodingResults(email);
    }

    // ✅ Start Attempt (NOW STORES IN DB ✅)
    @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
    @PostMapping("/v1/start-attempt/{testId}")
    public ResponseEntity<Map<String, String>> startAttempt(@PathVariable Long testId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        String attemptId = service.startAttempt(testId, email);

        return ResponseEntity.ok(Map.of("attemptId", attemptId));
    }

    // ✅ Submit Code
    @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitCode(
            @RequestBody Map<String, Object> request) {

        try {
            Long questionId = ((Number) request.get("questionId")).longValue();
            String sourceCode = (String) request.get("sourceCode");
            Integer languageId = ((Number) request.get("languageId")).intValue();
            String attemptId = (String) request.get("attemptId");

            String email = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

            Map<String, Object> result = service.submitCodingAnswer(
                    questionId,
                    sourceCode,
                    languageId,
                    attemptId,
                    email
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));
        }
    }
}