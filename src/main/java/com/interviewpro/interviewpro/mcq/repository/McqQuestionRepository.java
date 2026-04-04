package com.interviewpro.interviewpro.mcq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.mcq.entity.McqQuestion;

import java.util.List;

public interface McqQuestionRepository extends JpaRepository<McqQuestion, Long> {
	 int countByTest_TestId(Long testId);
    List<McqQuestion> findByTest_TestId(Long testId);
}
