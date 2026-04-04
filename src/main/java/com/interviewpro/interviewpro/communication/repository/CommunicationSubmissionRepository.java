package com.interviewpro.interviewpro.communication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interviewpro.interviewpro.communication.entity.CommunicationSubmission;
import com.interviewpro.interviewpro.communication.enums.SubmissionStatus;

@Repository
public interface CommunicationSubmissionRepository extends JpaRepository<CommunicationSubmission, Long> {

    List<CommunicationSubmission> findByStatus(SubmissionStatus status);

    List<CommunicationSubmission> findByUserId(String userId);

    List<CommunicationSubmission> findByUserIdAndStatus(String userId, SubmissionStatus status);

    boolean existsByTestIdAndStatus(Long testId, SubmissionStatus status);
    @Query("""
        SELECT s
        FROM CommunicationSubmission s
        LEFT JOIN FETCH s.answers
        WHERE s.id = :id
    """)
    Optional<CommunicationSubmission> findByIdWithAnswers(Long id);
}