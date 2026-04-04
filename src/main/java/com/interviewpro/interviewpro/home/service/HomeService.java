package com.interviewpro.interviewpro.home.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.coding.entity.CodingTest;
import com.interviewpro.interviewpro.coding.repository.CodingTestRepository;
import com.interviewpro.interviewpro.communication.enums.TestStatus;
import com.interviewpro.interviewpro.communication.repository.CommunicationTestRepository;
import com.interviewpro.interviewpro.home.dto.HomeStatsResponse;
import com.interviewpro.interviewpro.mcq.dto.response.TestListResponse;
import com.interviewpro.interviewpro.mcq.enums.TestType;
import com.interviewpro.interviewpro.mcq.repository.McqTestRepository;

@Service
public class HomeService {
	 @Autowired 
	    private McqTestRepository mcqRepo;
	 
	 @Autowired 
	 private CodingTestRepository codingRepo;
	 
	 @Autowired
	 private CommunicationTestRepository communicationRepo;

	    private String getCurrentUserId() {
	        return SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();
	    }

	 public List<TestListResponse> getAllPublicTests() {
		   
		    
	     return mcqRepo.findByIsPublicTrueOrderByTestIdDesc()
	             .stream()
	             .map(test -> TestListResponse.builder()
	                     .testId(test.getTestId())
	                     .title(test.getTitle())
	                     .durationMinutes(test.getDurationMinutes())
	                     .build())
	             .toList();
	 }

	 public List<TestListResponse> getAllPublicTests(TestType testType) {

		    String createdBy = getCurrentUserId();

		    // CODING TESTS
		    if (testType == TestType.CODING) {

		        return codingRepo.findByPublicTestTrue()
		                .stream()
		                .map(test -> TestListResponse.builder()
		                        .testId(test.getTestId())
		                        .title(test.getTitle())
		                        .durationMinutes(test.getDurationMinutes())
		                        .publicTest(test.isPublicTest())
		                        .testType("CODING")
		                        .totalQuestions(test.getTotalQuestions())
		                        .build())
		                .toList();
		    }

		    // COMMUNICATION TESTS
		    if (testType == TestType.COMMUNICATION) {

		        return communicationRepo.findByStatus(TestStatus.ACTIVE)
		                .stream()
		                .map(test -> TestListResponse.builder()
		                        .testId(test.getId())
		                        .title(test.getTitle())
		                        .durationMinutes(0) // communication usually not timed
		                        .totalQuestions(test.getTotalQuestions())
		                        .publicTest(true)
		                        .testType("COMMUNICATION")
		                        .build())
		                .toList();
		    }

		    // MCQ TESTS
		    return mcqRepo.findByIsPublicTrueAndTestTypeOrderByTestIdDesc(testType)
		            .stream()
		            .map(test -> TestListResponse.builder()
		                    .testId(test.getTestId())
		                    .title(test.getTitle())
		                    .durationMinutes(test.getDurationMinutes())
		                    .totalQuestions(test.getTotalQuestions())
		                    .testType(test.getTestType().toString())
		                    .publicTest(test.isPublic())
		                    .build())
		            .toList();
		}

	 public HomeStatsResponse getAllStats() {
		 TestStatus liveStatus = TestStatus.ACTIVE;
		 
		 int mcqTests = Math.toIntExact(mcqRepo.countByIsPublicTrue());
		 int codingTests = Math.toIntExact(codingRepo.countByPublicTestTrue());
		 int communicationTests = Math.toIntExact(communicationRepo.countByStatus(liveStatus));

		 int mcqQuestions = safe(mcqRepo.sumPublicQuestions());
		 int codingQuestions = safe(codingRepo.sumPublicQuestions());
		 int communicationQuestions = safe(communicationRepo.sumQuestionsByStatus(liveStatus));

		 int tests = mcqTests + codingTests + communicationTests;
		 int questions = mcqQuestions + codingQuestions + communicationQuestions;

		 return HomeStatsResponse.builder()
		         .rounds(4)
		         .questions(questions)
		         .tests(tests)
		         .build();
	 }
	 
	 private int safe(Integer value) {
		 return value == null ? 0 : value;
		 }
}
