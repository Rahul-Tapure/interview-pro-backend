package com.interviewpro.interviewpro.mcq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.mcq.entity.McqAttempt;

import java.util.List;

public interface McqAttemptRepository extends JpaRepository<McqAttempt, Long> {

    List<McqAttempt> findByStudentEmail(String studentEmail);
    boolean existsByTest_TestId(Long testId);

}
