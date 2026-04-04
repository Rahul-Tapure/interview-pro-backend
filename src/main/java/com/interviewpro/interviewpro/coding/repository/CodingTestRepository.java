package com.interviewpro.interviewpro.coding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.interviewpro.interviewpro.coding.entity.CodingTest;

public interface CodingTestRepository
extends JpaRepository<CodingTest, Long> {

List<CodingTest> findByCreatedBy(String createdBy);
List<CodingTest> findByPublicTestTrue();
List<CodingTest> findByPublicTestTrueAndCreatedBy(String createdBy);


long countByPublicTestTrue();

@Query("select coalesce(sum(t.totalQuestions), 0) from CodingTest t where t.publicTest = true")
Integer sumPublicQuestions();
}
