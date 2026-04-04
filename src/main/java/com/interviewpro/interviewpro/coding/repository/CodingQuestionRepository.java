package com.interviewpro.interviewpro.coding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.interviewpro.interviewpro.coding.entity.CodingQuestion;
import com.interviewpro.interviewpro.mcq.entity.McqTest;

@Repository
public interface CodingQuestionRepository
        extends JpaRepository<CodingQuestion, Long> {

    List<CodingQuestion> findByCreatedBy(String createdBy);
    Optional<CodingQuestion> findByCodingTest_TestIdAndQuestionIndex(
            Long testId,
            Integer questionIndex
    );

    List<CodingQuestion> findByCodingTest_TestId(Long testId);
    }
