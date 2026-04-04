package com.interviewpro.interviewpro.judge0.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.coding.entity.CodingTestCase;
import com.interviewpro.interviewpro.coding.repository.CodingQuestionRepository;
import com.interviewpro.interviewpro.coding.repository.CodingTestCaseRepository;
import com.interviewpro.interviewpro.judge0.dto.*;
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.judge0.enums.SubmissionStatus;
import com.interviewpro.interviewpro.judge0.repository.CodingSubmissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodingSubmitService {

    private final Judge0Service judge0Service;
    private final CodingTestCaseRepository testCaseRepo;
    private final CodingSubmissionRepository submissionRepo;
    private final CodingQuestionRepository questionRepo;

 // CodingSubmitService.java - Add better error handling
public SubmitResultResponse submit(SubmitCodeRequest request) {

    Authentication auth = SecurityContextHolder
            .getContext()
            .getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
        return SubmitResultResponse.builder()
                .passed(0)
                .total(0)
                .score(0)
                .status("AUTHENTICATION_REQUIRED")
                .message("User not authenticated")
                .build();
    }

    String email = auth.getName();

    // ✅ Fetch CodingQuestion
    var question = questionRepo.findById(request.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));

    // ✅ Get CodingTest from question
    var codingTest = question.getCodingTest();

    // ✅ Hidden test cases
    List<CodingTestCase> hiddenCases =
            testCaseRepo.findByCodingQuestionIdAndSampleFalse(request.getQuestionId());

    if (hiddenCases.isEmpty()) {
        return SubmitResultResponse.builder()
                .passed(0)
                .total(0)
                .score(0)
                .status("NO_TEST_CASES")
                .message("No hidden test cases found for this question")
                .build();
    }

    int passed = 0;
    double time = 0;
    int memory = 0;
    String error = null;

    for (CodingTestCase tc : hiddenCases) {

        Judge0Response res = judge0Service.execute(
                request.getSourceCode(),
                request.getLanguageId(),
                tc.getInput()
        );

        if (res.getStatus() == null || res.getStatus().getId() != 3) {
            error = res.getStderr() != null ? res.getStderr() : res.getCompileOutput();
            continue;
        }

        String output = res.getStdout() == null ? "" : res.getStdout().trim();

        if (output.equals(tc.getExpectedOutput().trim())) {
            passed++;
        }

        time = res.getTime();
        memory = res.getMemory();
    }

    int total = hiddenCases.size();
    int score = total == 0 ? 0 : (passed * 100) / total;

    SubmissionStatus status =
            passed == total ? SubmissionStatus.PASSED :
            error != null ? SubmissionStatus.ERROR :
            SubmissionStatus.FAILED;

    // ✅ SAVE with CodingTest relation
    CodingSubmission submission = CodingSubmission.builder()
            .questionId(request.getQuestionId())
            .codingTest(codingTest)   // ⭐ IMPORTANT LINE
            .userEmail(email)
            .sourceCode(request.getSourceCode())
            .languageId(request.getLanguageId())
            .totalTestCases(total)
            .passedTestCases(passed)
            .score(score)
            .status(status)
            .attemptId(request.getAttemptId())
            .executionTime(time)
            .memoryUsed(memory)
            .errorOutput(error)
            .submittedAt(LocalDateTime.now())
            .build();

    submissionRepo.save(submission);

    return SubmitResultResponse.builder()
            .passed(passed)
            .total(total)
            .score(score)
            .status(status.name())
            .message(error != null ? "Some test cases failed with errors" : null)
            .build();
}

}
