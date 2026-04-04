package com.interviewpro.interviewpro.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.auth.entity.AuthOtpCode;
import com.interviewpro.interviewpro.auth.entity.UserTable;

public interface AuthOtpCodeRepository extends JpaRepository<AuthOtpCode, String>{
	Optional<AuthOtpCode> findTopByOwnerEmailAndPurposeAndConsumedFalseOrderByCreatedAtDesc(
			String ownerEmail, String purpose
			);

			}