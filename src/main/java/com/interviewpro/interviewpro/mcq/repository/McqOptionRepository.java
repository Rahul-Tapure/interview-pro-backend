package com.interviewpro.interviewpro.mcq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.mcq.entity.McqOption;

import java.util.List;

public interface McqOptionRepository extends JpaRepository<McqOption, Long> {

    List<McqOption> findByQuestion_QuestionId(Long questionId);

}
