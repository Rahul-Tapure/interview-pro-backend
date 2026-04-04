package com.interviewpro.interviewpro.communication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interviewpro.interviewpro.communication.entity.CommunicationFeedback;

@Repository
public interface CommunicationFeedbackRepository extends JpaRepository<CommunicationFeedback, Long> {

    Optional<CommunicationFeedback> findByAnswerId(Long answerId);

    @Query("SELECT AVG(f.aiScore) FROM CommunicationFeedback f " +
           "JOIN f.answer a JOIN a.submission s WHERE s.userId = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);
}
