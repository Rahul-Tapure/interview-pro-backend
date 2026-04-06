package com.interviewpro.interviewpro.judge0.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;

public interface CodingSubmissionRepository extends JpaRepository<CodingSubmission, Long> {

    List<CodingSubmission> findByUserEmail(String email);

    List<CodingSubmission> findByQuestionId(Long questionId);
    List<CodingSubmission> findByUserEmailOrderBySubmittedAtDesc(String userEmail);
    Optional<CodingSubmission> findTopByUserEmailAndQuestionIdOrderByScoreDesc(
            String email, Long questionId
    );
    List<CodingSubmission> findByCodingTest_TestId(Long testId);
    List<CodingSubmission> findByUserEmailAndCodingTest_TestId(
            String email, Long testId);
    List<CodingSubmission> findByAttemptId(String attemptId);

}
