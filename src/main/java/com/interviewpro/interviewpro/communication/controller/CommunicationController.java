package com.interviewpro.interviewpro.communication.controller;

import com.interviewpro.interviewpro.auth.repository.UserRepository;
import com.interviewpro.interviewpro.communication.dto.request.*;
import com.interviewpro.interviewpro.communication.dto.response.*;
import com.interviewpro.interviewpro.communication.service.impl.CommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/interviewpro/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;
private final UserRepository userRepository;
    // ════════════════════════════════════════════════════════════════════════
    // TEST ENDPOINTS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * POST /api/v1/communication/tests
     * Create a new communication test with questions
     */
@PostMapping("/tests")
public ResponseEntity<TestResponse> createTest(@Valid @RequestBody CreateTestRequest request) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    request.setCreatedBy(email);

    TestResponse response = communicationService.createTest(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
    @PostMapping("/tests/{testId}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long testId,
            @RequestBody CreateQuestionRequest request) {

        QuestionResponse response = communicationService.addQuestion(testId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/tests/{testId}/questions/{step}")
    public ResponseEntity<QuestionResponse> getQuestion(
            @PathVariable Long testId,
            @PathVariable Integer step) {

        return ResponseEntity.ok(
                communicationService.getQuestion(testId, step));
    }
    @PutMapping("/tests/{testId}/questions/{step}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long testId,
            @PathVariable Integer step,
            @RequestBody UpdateQuestionRequest request) {

        return ResponseEntity.ok(
                communicationService.updateQuestion(testId, step, request));
    }
    
    @PutMapping("/tests/{testId}/publish")
    public ResponseEntity<TestResponse> publishTest(@PathVariable Long testId) {

        return ResponseEntity.ok(
                communicationService.publishTest(testId)
        );
    }
    @DeleteMapping("/tests/{testId}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {

        communicationService.deleteTest(testId);

        return ResponseEntity.noContent().build();
    }
    /**
     * GET /api/v1/communication/tests
     * Fetch all active tests
     */
    @GetMapping("/tests")
    public ResponseEntity<List<TestResponse>> getAllActiveTests() {

        return ResponseEntity.ok(
                communicationService.getAllActiveTests()
        );
    }
    @GetMapping("/tests/my")
    public ResponseEntity<List<TestResponse>> getAllActiveTestsByUser() {
    	String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        
        return ResponseEntity.ok(communicationService.getAllTestsByUser(email));
    }

    /**
     * GET /api/v1/communication/tests/{id}
     * Fetch a single test with all its questions
     */
    @GetMapping("/tests/{id}")
    public ResponseEntity<TestResponse> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(communicationService.getTestById(id));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMISSION ENDPOINTS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * POST /api/v1/communication/submissions/start
     * User begins a test attempt
     */
    @PostMapping("/submissions/start")
    public ResponseEntity<SubmissionResponse> startSubmission(
            @Valid @RequestBody StartSubmissionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        request.setUserId(email);  // store email directly
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communicationService.startSubmission(request));
    }
    /**
     * PUT /api/v1/communication/submissions/{id}/complete
     * Mark submission complete and compute overall score
     */
    @PutMapping("/submissions/{id}/complete")
    public ResponseEntity<SubmissionResponse> completeSubmission(@PathVariable Long id) {
        return ResponseEntity.ok(communicationService.completeSubmission(id));
    }
    @GetMapping("/submissions/my/completed")
    public ResponseEntity<List<SubmissionResponse>> getMyCompletedSubmissions() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(communicationService.getCompletedSubmissionsByEmail(email));
    }


    /**
     * GET /api/v1/communication/submissions/user/{userId}
     * Retrieve all submissions for a user
     */
    @GetMapping("/submissions/user/{userId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(communicationService.getSubmissionsByUser(userId));
    }
    

    // ════════════════════════════════════════════════════════════════════════
    // ANSWER ENDPOINTS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * POST /api/v1/communication/answers
     * Submit an answer (audio URL + AssemblyAI job ID) — Step 1 of pipeline
     *
     * Frontend flow:
     *   1. Record audio
     *   2. Upload audio to AssemblyAI directly (or via your backend)
     *   3. Send audio_url + assemblyai_job_id to this endpoint
     */
    @PostMapping("/answers")
    public ResponseEntity<AnswerResponse> submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communicationService.submitAnswer(request));
    }

    // ════════════════════════════════════════════════════════════════════════
    // ASSEMBLYAI WEBHOOK
    // ════════════════════════════════════════════════════════════════════════

    /**
     * POST /api/v1/communication/webhook/assemblyai
     * AssemblyAI calls this when transcription is done (Step 2 of pipeline)
     * Configure this URL in your AssemblyAI account webhook settings
     *
     * AssemblyAI POST body contains: { "transcript_id": "...", "status": "completed" }
     */
    @PostMapping("/webhook/assemblyai")
    public ResponseEntity<FeedbackResponse> handleAssemblyAIWebhook(
            @RequestBody Map<String, String> payload) {

        String jobId  = payload.get("transcript_id");
        String status = payload.get("status");

        if (!"completed".equalsIgnoreCase(status)) {
            return ResponseEntity.ok().build(); // Ignore non-completed events
        }

        FeedbackResponse feedback = communicationService.processTranscriptionComplete(jobId);
        return ResponseEntity.ok(feedback);
    }

    // ════════════════════════════════════════════════════════════════════════
    // FEEDBACK ENDPOINTS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/communication/feedback/answer/{answerId}
     * Fetch GPT feedback for a specific answer
     */
    @GetMapping("/feedback/answer/{answerId}")
    public ResponseEntity<FeedbackResponse> getFeedbackByAnswer(@PathVariable Long answerId) {
        return ResponseEntity.ok(communicationService.getFeedbackByAnswerId(answerId));
    }
    
    @PostMapping("/upload-audio")
    public ResponseEntity<Map<String, String>> uploadAudio(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(communicationService.uploadToAssemblyAI(file));
    }
    
    /**
     * GET /interviewpro/communication/submissions/user/{userId}/completed
     * Retrieve only COMPLETED submissions for a user
     */
    @GetMapping("/submissions/user/{userId}/completed")
    public ResponseEntity<List<SubmissionResponse>> getCompletedSubmissionsByUser(@PathVariable Long userId) {
       
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        
    
    	return ResponseEntity.ok(communicationService.getCompletedSubmissionsByUser(email));
    }
}