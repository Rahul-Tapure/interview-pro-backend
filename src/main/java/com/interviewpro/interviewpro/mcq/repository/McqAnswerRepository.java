package com.interviewpro.interviewpro.mcq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.mcq.entity.McqAnswer;

import java.util.List;

public interface McqAnswerRepository extends JpaRepository<McqAnswer, Long> {

    List<McqAnswer> findByAttempt_AttemptId(Long attemptId);
}
