package com.interviewpro.interviewpro.coding.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.judge0.enums.SubmissionStatus;
import com.interviewpro.interviewpro.judge0.repository.CodingSubmissionRepository;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CodingStudentService {

    private final CodingSubmissionRepository repository;

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
}
