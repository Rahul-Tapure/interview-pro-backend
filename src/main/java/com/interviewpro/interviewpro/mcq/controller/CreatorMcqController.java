package com.interviewpro.interviewpro.mcq.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewpro.interviewpro.mcq.dto.request.AddOptionRequest;
import com.interviewpro.interviewpro.mcq.dto.request.AddQuestionRequest;
import com.interviewpro.interviewpro.mcq.dto.request.CreateTestRequest;
import com.interviewpro.interviewpro.mcq.dto.request.UpdateFullTestRequest;
import com.interviewpro.interviewpro.mcq.dto.request.UpdateQuestionRequest;
import com.interviewpro.interviewpro.mcq.dto.response.OptionResponse;
import com.interviewpro.interviewpro.mcq.dto.response.PreviousQuestionResponse;
import com.interviewpro.interviewpro.mcq.dto.response.QuestionResponse;
import com.interviewpro.interviewpro.mcq.dto.response.TestDetailsResponse;
import com.interviewpro.interviewpro.mcq.dto.response.TestListResponse;
import com.interviewpro.interviewpro.mcq.dto.response.TestResponse;
import com.interviewpro.interviewpro.mcq.dto.response.TestViewResponse;
import com.interviewpro.interviewpro.mcq.service.CreatorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/interviewpro/mcq/v1/creator")
@PreAuthorize("hasRole('CREATOR')")
public class CreatorMcqController {

    @Autowired
    private CreatorService creatorService;

    
    /* =====================================================
       ✅ CREATOR APIs (PRIVATE TEST MANAGEMENT)
    ===================================================== */

    // ✅ Create Test (Default Private)
    @PreAuthorize("hasRole('CREATOR')")
    @PostMapping("/tests")
    public ResponseEntity<TestResponse> createTest(
            @Valid @RequestBody CreateTestRequest request) {

        String creatorEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                creatorService.createTest(request, creatorEmail)
        );
    }

    // ✅ Add Question (Only if Test is Private)
    @PreAuthorize("hasRole('CREATOR')")
    @PostMapping("/tests/{testId}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long testId,
            @Valid @RequestBody AddQuestionRequest request) {

        return ResponseEntity.ok(
                creatorService.addQuestion(testId, request)
        );
    }

    // ✅ Add Options (Only if Test is Private)
    @PreAuthorize("hasRole('CREATOR')")
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<List<OptionResponse>> addOptions(
            @PathVariable Long questionId,
            @Valid @RequestBody List<AddOptionRequest> request) {

        return ResponseEntity.ok(
                creatorService.addOptions(questionId, request)
        );
    }

    // ✅ Creator Dashboard → My Tests (Shows Public/Private) old
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/tests")
    public ResponseEntity<List<TestListResponse>> getMyTests() {

        String creatorEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                creatorService.getCreatorTests(creatorEmail)
        );
    }
    
 // ✅ Creator Dashboard → My Tests (Shows Public/Private) by type
    @GetMapping("/dashboard/tests")
    public List<TestListResponse> getMyTestsByType(
            @RequestParam String type
    ) {
    	   String creatorEmail = SecurityContextHolder
                   .getContext()
                   .getAuthentication()
                   .getName();

        return creatorService.getTestsByCreatorAndType(creatorEmail, type);
    }

    // ✅ View Test Details (Creator)
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/tests/{testId}")
    public ResponseEntity<TestDetailsResponse> getTestDetails(
            @PathVariable Long testId) {

        return ResponseEntity.ok(
                creatorService.getTestDetails(testId)
        );
    }
 // ✅ Creator Read-Only Full Test View
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/view/tests/{testId}")
    public ResponseEntity<TestViewResponse> viewFullTest(
            @PathVariable Long testId) {

        return ResponseEntity.ok(
                creatorService.viewTestDetails(testId)
        );
    }

    // ✅ Update Full Test (Only if Private)
    @PreAuthorize("hasRole('CREATOR')")
    @PutMapping("/tests/{testId}")
    public ResponseEntity<?> updateFullTest(
            @PathVariable Long testId,
            @RequestBody UpdateFullTestRequest request) {

        creatorService.updateFullTest(testId, request);

        return ResponseEntity.ok(
                Map.of("message", "✅ Test Updated Successfully")
        );
    }
    //Load Previous Question API
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<PreviousQuestionResponse> getPreviousQuestion(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                creatorService.getPreviousQuestion(questionId)
        );
    }

    //Update Question API
    @PreAuthorize("hasRole('CREATOR')")
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {

        return ResponseEntity.ok(
                creatorService.updateQuestion(questionId, request)
        );
    }
    
    //get queation by ids
    @GetMapping("/tests/{testId}/question-ids")
    @PreAuthorize("hasRole('CREATOR')")
    public List<Long> getQuestionIds(@PathVariable Long testId) {
        return creatorService.getQuestionIds(testId);
    }


    // ✅ Make Test Public (LOCK FOREVER)
    @PreAuthorize("hasRole('CREATOR')")
    @PutMapping("/tests/{testId}/publish")
    public ResponseEntity<?> publishTest(@PathVariable Long testId) {

        creatorService.publishTest(testId);

        return ResponseEntity.ok(
                Map.of("message", "✅ Test Published Successfully (Locked Forever)")
        );
    }

 /* =====================================================
    ✅ CREATOR: EDIT PRIVATE TEST ONLY
 ===================================================== */

 @PreAuthorize("hasRole('CREATOR')")
 @GetMapping("/tests/{testId}/edit")
 public ResponseEntity<UpdateFullTestRequest> loadTestForEdit(
         @PathVariable Long testId) {

     return ResponseEntity.ok(
             creatorService.loadTestForEdit(testId)
     );
 }

 @PreAuthorize("hasRole('CREATOR')")
 @PutMapping("/tests/{testId}/edit")
 public ResponseEntity<?> updatePrivateTest(
         @PathVariable Long testId,
         @RequestBody UpdateFullTestRequest request) {

     creatorService.updatePrivateTest(testId, request);

     return ResponseEntity.ok(
             Map.of("message", "✅ Private Test Updated Successfully")
     );
 }

    // ✅ Delete Test (Allowed Always)
    @PreAuthorize("hasRole('CREATOR')")
    @DeleteMapping("/tests/{testId}")
    public ResponseEntity<?> deleteTest(@PathVariable Long testId) {

        creatorService.deleteFullTest(testId);

        return ResponseEntity.ok(
                Map.of("message", "✅ Test Deleted Successfully")
        );
    }



    // other creator APIs stay same
}

