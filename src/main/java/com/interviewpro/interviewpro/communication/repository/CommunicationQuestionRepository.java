package com.interviewpro.interviewpro.communication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewpro.interviewpro.communication.entity.CommunicationQuestion;

@Repository
public interface CommunicationQuestionRepository extends JpaRepository<CommunicationQuestion, Long> {

    List<CommunicationQuestion> findByTestIdOrderByQuestionOrderAsc(Long testId);
    Optional<CommunicationQuestion> findByTestIdAndQuestionOrder(Long testId, Integer questionOrder);
    long countByTestId(Long testId);
}
