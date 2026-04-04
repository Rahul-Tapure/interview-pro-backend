package com.interviewpro.interviewpro.mcq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewpro.interviewpro.mcq.dto.request.SubmitTestRequest;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;
import com.interviewpro.interviewpro.mcq.dto.response.StartTestResponse;
import com.interviewpro.interviewpro.mcq.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/interviewpro/mcq/v1/student")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
public class StudentMcqController {

    @Autowired
    private StudentService studentService;

    /* =====================================================
    ✅ STUDENT APIs (PUBLIC TEST ACCESS)
 ===================================================== */

 // ✅ Student → Start Test (Only Public)
    @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
 @PostMapping("/tests/{testId}/start")
 public ResponseEntity<StartTestResponse> startTest(
         @PathVariable Long testId) {

     String studentEmail = SecurityContextHolder
             .getContext()
             .getAuthentication()
             .getName();

     return ResponseEntity.ok(
             studentService.startTest(testId, studentEmail)
     );
 }

 // ✅ Student → Submit Test
 @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
 @PostMapping("/attempts/{attemptId}/submit")
 public ResponseEntity<ResultResponse> submitTest(
         @PathVariable Long attemptId,
         @Valid @RequestBody SubmitTestRequest request) {

     return ResponseEntity.ok(
             studentService.submitTest(attemptId, request)
     );
 }

 // ✅ Student → My Attempts
 @PreAuthorize("hasAnyRole('STUDENT','CREATOR')")
 @GetMapping("/my-attempts")
 public ResponseEntity<List<ResultResponse>> myAttempts() {

     String studentEmail = SecurityContextHolder
             .getContext()
             .getAuthentication()
             .getName();

     return ResponseEntity.ok(
             studentService.getStudentResults(studentEmail)
     );
 }
}
