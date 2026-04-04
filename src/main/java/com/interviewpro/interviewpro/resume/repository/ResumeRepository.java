package com.interviewpro.interviewpro.resume.repository;

import com.interviewpro.interviewpro.resume.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<ResumeAnalysis, Long> {
	List<ResumeAnalysis> findByUsernameOrderByCreatedAtDesc(String username);

    Optional<ResumeAnalysis> findTopByUsernameOrderByCreatedAtDesc(String username);
}