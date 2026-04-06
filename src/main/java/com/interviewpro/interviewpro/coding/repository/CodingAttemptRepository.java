package com.interviewpro.interviewpro.coding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.coding.entity.CodingAttempt;

public interface CodingAttemptRepository extends JpaRepository<CodingAttempt, Long> {

    Optional<CodingAttempt> findByAttemptIdAndUserEmail(String attemptId, String email);

    List<CodingAttempt> findByUserEmail(String email);
}