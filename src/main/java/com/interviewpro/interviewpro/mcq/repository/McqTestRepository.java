package com.interviewpro.interviewpro.mcq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.interviewpro.interviewpro.mcq.entity.McqTest;
import com.interviewpro.interviewpro.mcq.enums.TestType;

import java.util.List;

public interface McqTestRepository extends JpaRepository<McqTest, Long> {
	
//	 @Query("SELECT t FROM McqTest t WHERE t.createdBy = :createdBy AND t.testType = :testTypeORDER BY t.testId DESC")
//	    	List<McqTest> findCreatorTestsByType(
//	    	        @Param("createdBy") String createdBy,
//	    	        @Param("testType") TestType testType
//	    	);
//	    	    
	List<McqTest> findByIsPublicTrueOrderByTestIdDesc();
	List<McqTest> findByIsPublicTrueAndTestTypeOrderByTestIdDesc(TestType testType);
    List<McqTest> findByCreatedBy(String createdBy);
    List<McqTest> findByCreatedByOrderByTestIdDesc(String createdBy);
    List<McqTest> findByTestTypeAndCreatedBy(TestType testType, String createdBy);
    

long countByIsPublicTrue();

@Query("select coalesce(sum(t.totalQuestions), 0) from McqTest t where t.isPublic = true")
Integer sumPublicQuestions();
}
