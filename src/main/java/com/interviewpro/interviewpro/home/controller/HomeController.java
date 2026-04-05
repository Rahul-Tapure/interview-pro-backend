package com.interviewpro.interviewpro.home.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewpro.interviewpro.home.dto.HomeStatsResponse;
import com.interviewpro.interviewpro.home.service.HomeService;
import com.interviewpro.interviewpro.mcq.dto.response.TestListResponse;
import com.interviewpro.interviewpro.mcq.enums.TestType;

@RestController
@RequestMapping("/interviewpro/home")
public class HomeController {

    @Autowired
    private HomeService commonService;

    /* =============================
       ✅ PUBLIC APIs (viewable without auth)
    ============================== */

    @GetMapping("/stats")
    public ResponseEntity<HomeStatsResponse> getAllStats() {
        return ResponseEntity.ok(commonService.getAllStats());
    }
    
    @GetMapping("/tests")
    public ResponseEntity<List<TestListResponse>> getAllPublicTests() {
        return ResponseEntity.ok(commonService.getAllPublicTests());
    }

    @GetMapping("/tests/by-type")
    public ResponseEntity<List<TestListResponse>> getPublicTestsByType(
            @RequestParam TestType type) {
    	
        return ResponseEntity.ok(commonService.getAllPublicTests(type));
    }
}
