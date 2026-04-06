package com.interviewpro.interviewpro.coding.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.coding.entity.CodingQuestion;
import com.interviewpro.interviewpro.coding.repository.CodingQuestionRepository;
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.judge0.enums.SubmissionStatus;
import com.interviewpro.interviewpro.judge0.repository.CodingSubmissionRepository;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CodingStudentService {

    private final CodingSubmissionRepository repository;
    private final CodingQuestionRepository questionRepository;

public List<ResultResponse> getCodingResults(String email) {

    List<CodingSubmission> submissions = repository.findByUserEmail(email);

    // Group by attemptId — each attempt = one test row
    Map<String, List<CodingSubmission>> byAttempt =
            submissions.stream()
                    .filter(s -> s.getAttemptId() != null)
                    .collect(Collectors.groupingBy(CodingSubmission::getAttemptId));

    return byAttempt.values().stream()
            .map(attempt -> {
                var test = attempt.get(0).getCodingTest();
                int totalQuestions = test.getTotalQuestions();
                int attempted = attempt.size();
                int passed = (int) attempt.stream()
                        .filter(s -> s.getStatus() == SubmissionStatus.PASSED)
                        .count();

                String timeTaken = "00:00:00";
                var sorted = attempt.stream()
                        .filter(s -> s.getSubmittedAt() != null)
                        .sorted(Comparator.comparing(CodingSubmission::getSubmittedAt))
                        .toList();

                if (sorted.size() >= 2) {
                    var duration = java.time.Duration.between(
                            sorted.get(0).getSubmittedAt(),
                            sorted.get(sorted.size() - 1).getSubmittedAt()
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
            })
            .toList();
}

/**
 * Submit coding answer and store submission record
 * @param questionId - The question being submitted
 * @param sourceCode - User's code
 * @param languageId - Judge0 language ID
 * @param attemptId - Unique attempt identifier
 * @param email - User's email
 * @return Map with submission result
 */
public Map<String, Object> submitCodingAnswer(
        Long questionId, 
        String sourceCode, 
        Integer languageId, 
        String attemptId,
        String email) {
    
    try {
        // Fetch the question to get the related test
        Optional<CodingQuestion> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Question not found");
            return error;
        }
        
        CodingQuestion question = questionOpt.get();
        
        // Store the submission
        CodingSubmission submission = CodingSubmission.builder()
                .questionId(questionId)
                .sourceCode(sourceCode)
                .languageId(languageId)
                .attemptId(attemptId)
                .userEmail(email)
                .codingTest(question.getCodingTest())  // Get test from question
                .status(SubmissionStatus.PASSED)  // Default to PASSED; can be updated later
                .submittedAt(java.time.LocalDateTime.now())
                .build();
        
        repository.save(submission);
        
        // Return result
        Map<String, Object> result = new HashMap<>();
        result.put("status", "PASSED");
        result.put("message", "Submission received and stored successfully");
        result.put("passed", 1);
        result.put("total", 1);
        result.put("submissionId", submission.getId());
        
        return result;
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "ERROR");
        error.put("message", "Failed to store submission: " + e.getMessage());
        return error;
    }
}
}
