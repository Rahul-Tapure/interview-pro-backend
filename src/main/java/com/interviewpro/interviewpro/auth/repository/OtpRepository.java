package com.interviewpro.interviewpro.auth.repository;

import com.interviewpro.interviewpro.auth.entity.OtpRecord;
import com.interviewpro.interviewpro.auth.entity.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpRecord, Long> {
    Optional<OtpRecord> findTopByEmailAndTypeAndUsedFalseOrderByIdDesc(String email, OtpType type);
    void deleteByEmailAndType(String email, OtpType type);
}