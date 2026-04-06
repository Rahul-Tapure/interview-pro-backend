package com.interviewpro.interviewpro.coding.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.coding.entity.CodingAttempt;
import com.interviewpro.interviewpro.coding.entity.CodingQuestion;
import com.interviewpro.interviewpro.coding.entity.CodingTest;
import com.interviewpro.interviewpro.coding.repository.CodingAttemptRepository;
import com.interviewpro.interviewpro.coding.repository.CodingQuestionRepository;
import com.interviewpro.interviewpro.coding.repository.CodingTestRepository;
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.judge0.enums.SubmissionStatus;
import com.interviewpro.interviewpro.judge0.repository.CodingSubmissionRepository;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CodingStudentService {
	private final CodingAttemptRepository attemptRepository;
	private final CodingTestRepository testRepository;
    private final CodingSubmissionRepository repository;
    private final CodingQuestionRepository questionRepository;

public List<ResultResponse> getCodingResults(String email) {

    List<CodingAttempt> attempts = attemptRepository.findByUserEmail(email);

    return attempts.stream().map(attempt -> {

        List<CodingSubmission> submissions =
                repository.findByAttemptId(attempt.getAttemptId());

        CodingTest test = attempt.getCodingTest();

        int totalQuestions = test.getTotalQuestions();
        int attempted = submissions.size();

        int passed = (int) submissions.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.PASSED)
                .count();

        // ✅ Time calculation
        String timeTaken = "00:00:00";

        if (attempt.getStartTime() != null && !submissions.isEmpty()) {

            LocalDateTime endTime = submissions.stream()
                    .map(CodingSubmission::getSubmittedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(attempt.getStartTime());

            Duration duration = Duration.between(
                    attempt.getStartTime(),
                    endTime
            );

            timeTaken = String.format("%02d:%02d:%02d",
                    duration.toHours(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart());
        }

        return ResultResponse.builder()
                .title(test.getTitle())
                .type("CODING")
                .score(passed)
                .totalQuestions(totalQuestions)
                .correct(passed)
                .wrong(attempted - passed)
                .unattempted(totalQuestions - attempted)
                .timeTaken(timeTaken)
                .passed(passed >= Math.ceil(totalQuestions * 0.4))
                .message("Completed")
                .build();

    }).toList();
}
public String startAttempt(Long testId, String email) {

    CodingTest test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found"));

    String attemptId = UUID.randomUUID().toString();

    CodingAttempt attempt = CodingAttempt.builder()
            .attemptId(attemptId)
            .userEmail(email)
            .codingTest(test)
            .startTime(LocalDateTime.now())
            .build();

    attemptRepository.save(attempt);

    return attemptId;
}
public Map<String, Object> submitCodingAnswer(
        Long questionId,
        String sourceCode,
        Integer languageId,
        String attemptId,
        String email) {

    if (attemptId == null || attemptId.isEmpty()) {
        throw new RuntimeException("Invalid attemptId");
    }

    // ✅ Validate attempt exists
    CodingAttempt attempt = attemptRepository
            .findByAttemptIdAndUserEmail(attemptId, email)
            .orElseThrow(() -> new RuntimeException("Attempt not found"));

    CodingQuestion question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));

    // ✅ Save submission
    CodingSubmission submission = CodingSubmission.builder()
            .questionId(questionId)
            .sourceCode(sourceCode)
            .languageId(languageId)
            .attemptId(attemptId)
            .userEmail(email)
            .codingTest(question.getCodingTest())
            .status(SubmissionStatus.PASSED) // later replace with Judge0
            .submittedAt(LocalDateTime.now())
            .build();

    repository.save(submission);

    return Map.of(
            "status", "PASSED",
            "message", "Submission saved",
            "passed", 1,
            "total", 1
    );
}
}
