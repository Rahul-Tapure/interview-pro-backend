package com.interviewpro.interviewpro.coding.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

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
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/interviewpro/coding/v1/creator")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CREATOR')")
public class CodingCreatorController {

	@Autowired
    private final CodingCreatorService service;

    /* ==========================================================
                        ✅ CODING TEST APIs
       ========================================================== */

    // ✅ Create Coding Test (Same UI as MCQ)
    @PostMapping("/tests")
    public CodingTest createTest(@RequestBody CodingTestRequest request) {
        return service.createCodingTest(request);
    }

    // ✅ Get My Coding Tests (Dashboard)
    @GetMapping("/tests")
    public List<CodingTestListResponse> getMyTests() {
        return service.getMyCodingTests();
    }
    
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/tests/{testId}/questions")
    public List<CodingQuestionResponse> getMyTestsQuestion(@PathVariable Long testId) {
        return service.getMyCodingTestsQuestion(testId);
    }
    
    @PostMapping("/start-attempt/{testId}")
    public ResponseEntity<Map<String, String>> startAttempt(@PathVariable Long testId) {
        String attemptId = java.util.UUID.randomUUID().toString();
        return ResponseEntity.ok(Map.of("attemptId", attemptId));
    }
    
    /* ✅ Update Test */
    @PutMapping("/tests/{id}")
    public CodingTestResponse updateTest(
            @PathVariable Long id,
            @RequestBody CodingTestRequest request) {

        return service.updateCodingTest(id, request);
    }


    // ✅ Get Test By ID (View Button)
    @GetMapping("/tests/{id}")
    public CodingTestResponse getTestById(@PathVariable Long id) {
        return service.getCodingTestById(id);
    }

    /* ✅ Publish Full Test */
    @PostMapping("/tests/{id}/publish")
    public ResponseEntity<Void> publishTest(@PathVariable Long id) {
        service.publishCodingTest(id);
        return ResponseEntity.ok().build();
    }

    // ✅ Delete Full Coding Test (Test + Questions + TestCases)
    @DeleteMapping("/tests/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        service.deleteCodingTest(id);
        return ResponseEntity.ok().build();
    }

    /* ==========================================================
                        ✅ CODING QUESTION APIs
       ========================================================== */

    // ✅ Add Question Under Test
    @PostMapping("/tests/{testId}/questions")
    public CodingQuestionResponse createQuestion(
            @PathVariable Long testId,
            @RequestParam Integer step,
            @RequestBody CodingQuestionRequest request
    ) {
        return service.createQuestion(testId, step, request);
    }



    /* ✅ Update Question */
    @PutMapping("/questions/{id}")
    public CodingQuestion updateQuestion(
            @PathVariable Long id,
            @RequestBody CodingQuestionRequest request) {

        return service.updateQuestion(id, request);
    }

    // ✅ Get Questions By Test
    @GetMapping("/tests/{testId}/question")
    public ResponseEntity<CodingQuestionResponse> getQuestionByStep(
            @PathVariable Long testId,
            @RequestParam Integer step
    ) {
        return ResponseEntity.ok(service.getQuestionByStep(testId, step));
    }

    // ✅ Get Single Question
    @GetMapping("/questions/{id}")
    public CodingQuestionResponse getQuestion(@PathVariable Long id) {
        return service.getQuestionResponse(id);
    }

    // ✅ Delete Question (Also deletes TestCases)
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        service.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    /* ==========================================================
                        ✅ TEST CASE APIs
       ========================================================== */

    // ✅ Add TestCase to Question
    @PostMapping("/questions/{id}/test-cases")
    public CodingTestCaseResponse addTestCase(
            @PathVariable Long id,
            @RequestBody CodingTestCaseRequest request) {

        return service.addTestCase(id, request);
    }

    /* ✅ Update TestCase */
    @PutMapping("/test-cases/{id}")
    public CodingTestCase updateTestCase(
            @PathVariable Long id,
            @RequestBody CodingTestCaseRequest request) {

        return service.updateTestCase(id, request);
    }

    
    // ✅ Get TestCases of Question
    @GetMapping("/questions/{id}/test-cases")
    public List<CodingTestCaseResponse> getTestCases(@PathVariable Long id) {
        return service.getTestCaseResponses(id);
    }

    // ✅ Delete TestCase
    @DeleteMapping("/test-cases/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        service.deleteTestCase(id);
        return ResponseEntity.ok().build();
    }


}
