package com.interviewpro.interviewpro.communication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interviewpro.interviewpro.communication.entity.CommunicationTest;
import com.interviewpro.interviewpro.communication.enums.TestStatus;

@Repository
public interface CommunicationTestRepository extends JpaRepository<CommunicationTest, Long> {

    // Students see only published tests
    List<CommunicationTest> findByStatus(TestStatus status);

    // Creator dashboard
    List<CommunicationTest> findByCreatedByAndStatusNot(String createdBy, TestStatus status);

    Optional<CommunicationTest> findByIdAndStatusNot(Long id, TestStatus status);

    List<CommunicationTest> findByCreatedBy(String createdBy);

	@Query("""
		    SELECT t
		    FROM CommunicationTest t
		    LEFT JOIN FETCH t.questions
		    WHERE t.id = :id
		""")
		Optional<CommunicationTest> findByIdWithQuestions(@Param("id") Long id);
	
	long countByStatus(TestStatus status);

	@Query("select coalesce(sum(t.totalQuestions), 0) from CommunicationTest t where t.status = :status")
	Integer sumQuestionsByStatus(@Param("status") TestStatus status);
	
}
