package com.interviewpro.interviewpro.resume.controller;


import com.interviewpro.interviewpro.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    
    // 1️⃣ Analyze & Save Resume
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(
            @RequestParam String roles,
            @RequestParam MultipartFile file
    ) throws Exception {
        return resumeService.analyzeResume(roles, file);
    }

    // 2️⃣ Get Resume By ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws Exception {
        return resumeService.getResumeById(id);
    }

    // 3️⃣ Get All Resumes of Logged-in User
    @GetMapping("/my")
    public ResponseEntity<?> getMyResumes() {
        return resumeService.getUserResumes();
    }

    // 4️⃣ Get Latest Resume
    @GetMapping("/latest")
    public ResponseEntity<?> getLatest() throws Exception {
        return resumeService.getLastReport();
    }

    // 5️⃣ Delete Resume
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return resumeService.deleteResume(id);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) throws Exception {
        return resumeService.downloadReport(id);
    }}