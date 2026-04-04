package com.interviewpro.interviewpro.coding.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.coding.dto.*;
import com.interviewpro.interviewpro.coding.entity.*;
import com.interviewpro.interviewpro.coding.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodingCreatorService {

    private final CodingTestRepository testRepo;
    private final CodingQuestionRepository questionRepo;
    private final CodingTestCaseRepository testCaseRepo;

    /* ================= CURRENT USER ================= */

    private String getCurrentUserId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    /* ==========================================================
                    ✅ CODING TEST SERVICE
       ========================================================== */

    // ✅ Create Coding Test (Same UI as MCQ)
    public CodingTest createCodingTest(CodingTestRequest req) {

        CodingTest test = CodingTest.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .testType(req.getTestType())
                .durationMinutes(req.getDurationMinutes())
                .totalQuestions(req.getTotalQuestions())
                .publicTest(false)
                .createdBy(getCurrentUserId())
                .build();

        return testRepo.save(test);
    }

    //update test
    public CodingTestResponse updateCodingTest(Long id, CodingTestRequest request) {

        CodingTest test = testRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (Boolean.TRUE.equals(test.isPublicTest())) {
            throw new RuntimeException("Published test cannot be edited");
        }

        test.setTitle(request.getTitle());
        test.setDurationMinutes(request.getDurationMinutes());

        testRepo.save(test);

        // ✅ return DTO (no recursion)
        return CodingTestResponse.builder()
                .testId(test.getTestId())
                .title(test.getTitle())
                .durationMinutes(test.getDurationMinutes())
                .publicTest(test.isPublicTest())
                .build();
    }

    // ✅ Get My Tests (Dashboard)
    public List<CodingTestListResponse> getMyCodingTests() {

        String createdBy = getCurrentUserId();

        return testRepo.findByCreatedBy(createdBy)
                .stream()
                .map(test -> CodingTestListResponse.builder()
                        .testId(test.getTestId())   // ✅ fixed
                        .title(test.getTitle())
                        .durationMinutes(test.getDurationMinutes())
                        .publicTest(test.isPublicTest())
                        .testType("CODING")
                        .build()
                )
                .toList();
    }

    

    // ✅ View Test by ID (View Button)
    public CodingTestResponse getCodingTestById(Long testId) {

        CodingTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // ownership check
        if (!test.getCreatedBy().equals(getCurrentUserId())) {
            throw new RuntimeException("Not allowed to access this test");
        }

        return CodingTestResponse.builder()
                .testId(test.getTestId())
                .title(test.getTitle())
                .description(test.getDescription())
                .durationMinutes(test.getDurationMinutes())
                .totalQuestions(test.getTotalQuestions())
                .publicTest(test.isPublicTest())
                .testType(test.getTestType().name())
                .build();
    }
    
    
    public List<CodingQuestionResponse> getMyCodingTestsQuestion(Long testId) {

        return questionRepo.findByCodingTest_TestId(testId)
                .stream()
                .map(q -> CodingQuestionResponse.builder()
                        .questionId(q.getId())                 // ✅ FIXED (id → questionId)
                        .title(q.getTitle())
                        .difficulty(q.getDifficulty())         // ✅ FIXED (String, not enum)
                        .description(q.getDescription())
                        .constraints(q.getConstraints())
                        .timeLimit(q.getTimeLimit())
                        .memoryLimit(q.getMemoryLimit())
                        .published(q.getPublished())
                        .createdBy(q.getCreatedBy())
                        .questionIndex(q.getQuestionIndex())

                        // ✅ Map test cases safely (avoid LAZY crash)
                        .testCases(
                            q.getTestCases() == null ? List.of() :
                            q.getTestCases().stream()
                                .map(tc -> CodingTestCaseResponse.builder()
                                        .id(tc.getId())
                                        .input(tc.getInput())
                                        .expectedOutput(tc.getExpectedOutput())
                                        .sample(tc.getSample())
                                        .build())
                                .toList()
                        )
                        .build()
                )
                .toList();
    }

    // ✅ Delete Full Test (Test + Questions + TestCases)
    @Transactional
    public void deleteCodingTest(Long testId) {

        CodingTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.getCreatedBy().equals(getCurrentUserId())) {
            throw new RuntimeException("Not allowed to delete this test");
        }

        // cascade will delete questions + testcases
        testRepo.delete(test);
    }

    /* ==========================================================
                    ✅ CODING QUESTION SERVICE
       ========================================================== */

    // ✅ Create Question Under Test
    public CodingQuestionResponse createQuestion(
            Long testId,
            Integer step,
            CodingQuestionRequest req
    ) {

        CodingTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        CodingQuestion q = new CodingQuestion();
        q.setTitle(req.getTitle());
        q.setDescription(req.getDescription());
        q.setDifficulty(req.getDifficulty());
        q.setConstraints(req.getConstraints());
        q.setTimeLimit(req.getTimeLimit());
        q.setMemoryLimit(req.getMemoryLimit());

        q.setQuestionIndex(step);
        q.setCreatedBy(getCurrentUserId());
        q.setCodingTest(test);

        CodingQuestion saved = questionRepo.save(q);

        // ✅ Return DTO (NO recursion)
        return CodingQuestionResponse.builder()
                .questionId(saved.getId())
                .title(saved.getTitle())
                .difficulty(saved.getDifficulty())
                .description(saved.getDescription())
                .constraints(saved.getConstraints())
                .timeLimit(saved.getTimeLimit())
                .memoryLimit(saved.getMemoryLimit())
                .questionIndex(saved.getQuestionIndex())
                .published(saved.getPublished())
                .build();
    }
//update question
    public CodingQuestion updateQuestion(Long questionId, CodingQuestionRequest req) {

        CodingQuestion q = getQuestion(questionId);

        if (q.getCodingTest().isPublicTest()) {
            throw new IllegalStateException("Cannot edit question after test is published");
        }

        q.setTitle(req.getTitle());
        q.setDifficulty(req.getDifficulty());
        q.setDescription(req.getDescription());
        q.setConstraints(req.getConstraints());
        q.setTimeLimit(req.getTimeLimit());
        q.setMemoryLimit(req.getMemoryLimit());

        return questionRepo.save(q);
    }

    // ✅ Questions By Test
    public List<CodingQuestionResponse> getQuestionsByTest(Long testId) {

        return questionRepo.findByCodingTest_TestId(testId)
                .stream()
                .map(q -> {
                    CodingQuestionResponse dto = new CodingQuestionResponse();
                    dto.setQuestionId(q.getId());
                    dto.setTitle(q.getTitle());
                    dto.setDifficulty(q.getDifficulty());
                    dto.setPublished(q.getPublished());
                    return dto;
                })
                .toList();
    }

    // ✅ Get Single Question
    public CodingQuestion getQuestion(Long id) {
        return questionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    // ✅ Get Question Response
    public CodingQuestionResponse getQuestionResponse(Long id) {

        CodingQuestion q = getQuestion(id);

        CodingQuestionResponse res = new CodingQuestionResponse();
        res.setQuestionId(q.getId());
        res.setTitle(q.getTitle());
        res.setDifficulty(q.getDifficulty());
        res.setDescription(q.getDescription());
        res.setConstraints(q.getConstraints());
        res.setTimeLimit(q.getTimeLimit());
        res.setMemoryLimit(q.getMemoryLimit());
        res.setPublished(q.getPublished());

        // ✅ test cases included
        List<CodingTestCaseResponse> tcList = q.getTestCases()
                .stream()
                .map(tc -> {
                    CodingTestCaseResponse dto = new CodingTestCaseResponse();
                    dto.setId(tc.getId());
                    dto.setInput(tc.getInput());
                    dto.setExpectedOutput(tc.getExpectedOutput());
                    dto.setSample(tc.getSample());
                    return dto;
                })
                .toList();

        res.setTestCases(tcList);

        return res;
    }
    public CodingQuestionResponse getQuestionByStep(Long testId, Integer step) {

        return questionRepo
                .findByCodingTest_TestIdAndQuestionIndex(testId, step)
                .map(this::mapToResponse)
                .orElse(null); // ✅ return null if not created yet
    }

    private CodingQuestionResponse mapToResponse(CodingQuestion q) {

        return CodingQuestionResponse.builder()
                .questionId(q.getId())
                .title(q.getTitle())
                .difficulty(q.getDifficulty())
                .description(q.getDescription())
                .constraints(q.getConstraints())
                .timeLimit(q.getTimeLimit())
                .memoryLimit(q.getMemoryLimit())
                .questionIndex(q.getQuestionIndex())
                .build();
    }
    // ✅ Delete Question
    public void deleteQuestion(Long id) {
        questionRepo.deleteById(id);
    }

    /* ==========================================================
                    ✅ TEST CASE SERVICE
       ========================================================== */

    // ✅ Add Test Case
    public CodingTestCaseResponse addTestCase(Long questionId, CodingTestCaseRequest req) {

        CodingQuestion question = getQuestion(questionId);

        if (question.getPublished()) {
            throw new IllegalStateException("Cannot modify published question");
        }

        CodingTestCase tc = new CodingTestCase();
        tc.setCodingQuestion(question);
        tc.setInput(req.getInput());
        tc.setExpectedOutput(req.getExpectedOutput());
        tc.setSample(Boolean.TRUE.equals(req.getSample()));

        CodingTestCase dbRes = testCaseRepo.save(tc);
        
        return CodingTestCaseResponse.builder()
        		.id(dbRes.getId())
        		.input(dbRes.getInput())
        		.expectedOutput(dbRes.getExpectedOutput())
        		.sample(dbRes.getSample())
        		.build();
   
    }
    
    //update testCase
    public CodingTestCase updateTestCase(Long testCaseId, CodingTestCaseRequest req) {

        CodingTestCase tc = testCaseRepo.findById(testCaseId)
                .orElseThrow(() -> new RuntimeException("TestCase not found"));

        if (tc.getCodingQuestion().getCodingTest().isPublicTest()) {
            throw new IllegalStateException("Cannot edit test case after test is published");
        }

        tc.setInput(req.getInput());
        tc.setExpectedOutput(req.getExpectedOutput());
        tc.setSample(req.getSample());

        return testCaseRepo.save(tc);
    }


    // ✅ Get TestCase Responses
    public List<CodingTestCaseResponse> getTestCaseResponses(Long questionId) {

        return testCaseRepo.findByCodingQuestion_Id(questionId)
                .stream()
                .map(tc -> {
                    CodingTestCaseResponse dto = new CodingTestCaseResponse();
                    dto.setId(tc.getId());
                    dto.setInput(tc.getInput());
                    dto.setExpectedOutput(tc.getExpectedOutput());
                    dto.setSample(tc.getSample());
                    return dto;
                })
                .toList();
    }

    // ✅ Delete Test Case
    public void deleteTestCase(Long id) {
        testCaseRepo.deleteById(id);
    }

    /* ==========================================================
                    ✅ PUBLISH Test
       ========================================================== */

    @Transactional
    public void publishCodingTest(Long testId) {

        CodingTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.getCreatedBy().equals(getCurrentUserId())) {
            throw new RuntimeException("Not allowed");
        }

        if (test.isPublicTest()) {
            throw new IllegalStateException("Test already published");
        }

        if (test.getQuestions().isEmpty()) {
            throw new IllegalStateException("Add questions before publishing");
        }

        // ✅ Lock test
        test.setPublicTest(true);
        testRepo.save(test);
    }

}
