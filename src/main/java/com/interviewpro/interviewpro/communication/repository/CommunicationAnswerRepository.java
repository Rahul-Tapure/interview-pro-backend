package com.interviewpro.interviewpro.communication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewpro.interviewpro.communication.entity.CommunicationAnswer;
import com.interviewpro.interviewpro.communication.enums.TranscriptionStatus;

@Repository
public interface CommunicationAnswerRepository extends JpaRepository<CommunicationAnswer, Long> {

    List<CommunicationAnswer> findBySubmissionId(Long submissionId);

    Optional<CommunicationAnswer> findByAssemblyaiJobId(String assemblyaiJobId);

    List<CommunicationAnswer> findByTranscriptionStatus(TranscriptionStatus status);

    Optional<CommunicationAnswer> findBySubmissionIdAndQuestionId(Long submissionId, Long questionId);
}