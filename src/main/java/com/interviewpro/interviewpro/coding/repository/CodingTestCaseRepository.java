package com.interviewpro.interviewpro.coding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewpro.interviewpro.coding.entity.CodingTestCase;

@Repository
public interface CodingTestCaseRepository
        extends JpaRepository<CodingTestCase, Long> {

    List<CodingTestCase> findByCodingQuestion_Id(Long questionId);

    //change nedded
    List<CodingTestCase> findByIdAndSampleFalse(Long questionId);

    // 🔹 Hidden test cases for submit
    List<CodingTestCase> findByCodingQuestionIdAndSampleFalse(Long questionId);

    // 🔹 Sample test cases for run (optional but useful)
    List<CodingTestCase> findByCodingQuestionIdAndSampleTrue(Long questionId);
}
