package com.interviewpro.interviewpro.communication.service.impl;

import com.interviewpro.interviewpro.auth.repository.UserRepository;
import com.interviewpro.interviewpro.communication.dto.request.*;
import com.interviewpro.interviewpro.communication.dto.response.*;
import com.interviewpro.interviewpro.communication.entity.*;
import com.interviewpro.interviewpro.communication.enums.SubmissionStatus;
import com.interviewpro.interviewpro.communication.enums.TestStatus;
import com.interviewpro.interviewpro.communication.enums.TranscriptionStatus;
import com.interviewpro.interviewpro.communication.repository.*;
import com.interviewpro.interviewpro.communication.service.AssemblyAIService;
import com.interviewpro.interviewpro.communication.service.GeminiService;
import com.interviewpro.interviewpro.communication.service.OpenAIService;
import com.interviewpro.interviewpro.communication.service.WebhookUrlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class CommunicationService {

    private final CommunicationTestRepository       testRepository;
    private final CommunicationQuestionRepository   questionRepository;
    private final CommunicationSubmissionRepository submissionRepository;
    private final CommunicationAnswerRepository     answerRepository;
    private final CommunicationFeedbackRepository   feedbackRepository;
    private final UserRepository userRepository;
    private final AssemblyAIService assemblyAIService;
    private final GeminiService geminiService;
    private final WebhookUrlService webhookUrlService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${assemblyai.api.key}")
    private String assemblyApiKey;

    // ════════════════════════════════════════════════════════════════════════
    // TEST
    // ════════════════════════════════════════════════════════════════════════

    @Transactional
    public TestResponse createTest(CreateTestRequest request) {

        CommunicationTest test = CommunicationTest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .totalQuestions(request.getTotalQuestions())
                .durationMinutes(request.getDurationMinutes())
                .status(TestStatus.DRAFT)
                .build();

        CommunicationTest saved = testRepository.save(test);

        return mapToTestResponse(saved);
    }
    
    @Transactional
    public QuestionResponse addQuestion(Long testId, CreateQuestionRequest request) {

        CommunicationTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (test.getQuestions().size() >= test.getTotalQuestions()) {
            throw new RuntimeException("Cannot add more than total questions");
        }

        CommunicationQuestion question = CommunicationQuestion.builder()
                .test(test)
                .questionText(request.getQuestionText())
                .timeLimit(request.getTimeLimit())
                .questionOrder(request.getQuestionOrder())
                .difficultyLevel(request.getDifficultyLevel())
                .category(request.getCategory())
                .build();

        CommunicationQuestion saved = questionRepository.save(question);

        return mapToQuestionResponse(saved);
    }
    
    @Transactional
    public QuestionResponse getQuestion(Long testId, Integer step) {

        CommunicationQuestion question = questionRepository
                .findByTestIdAndQuestionOrder(testId, step)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        return mapToQuestionResponse(question);
    }
    
    @Transactional
    public QuestionResponse updateQuestion(Long testId, Integer step, UpdateQuestionRequest request) {

        CommunicationQuestion question = questionRepository
                .findByTestIdAndQuestionOrder(testId, step)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        
        question.setQuestionText(request.getQuestionText());
        question.setTimeLimit(request.getTimeLimit());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setCategory(request.getCategory());

        CommunicationQuestion updated = questionRepository.save(question);

        return mapToQuestionResponse(updated);
    }
    
    public List<TestResponse> getAllActiveTests() {

        return testRepository
                .findByStatus(TestStatus.ACTIVE)
                .stream()
                .map(this::mapToTestResponse)
                .collect(Collectors.toList());
    }
    
    public TestResponse getTestById(Long id) {
        CommunicationTest test = testRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new RuntimeException("Test not found: " + id));
        return mapToTestResponse(test);
    }
    
    @Transactional
    public TestResponse publishTest(Long testId) {

        CommunicationTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if(test.getStatus() != TestStatus.DRAFT){
            throw new RuntimeException("Only draft tests can be published");
        }

        if(test.getQuestions().size() != test.getTotalQuestions()){
            throw new RuntimeException("All questions must be added before publishing");
        }

        test.setStatus(TestStatus.ACTIVE);

        CommunicationTest updated = testRepository.save(test);

        return mapToTestResponse(updated);
    }
    
    @Transactional
    public void deleteTest(Long testId) {

        CommunicationTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        boolean running =
                submissionRepository.existsByTestIdAndStatus(
                        testId,
                        SubmissionStatus.IN_PROGRESS
                );

        if(running){
            throw new RuntimeException("Test currently being attempted");
        }

        boolean attempted = !test.getSubmissions().isEmpty();

        if(attempted){
            test.setStatus(TestStatus.ARCHIVED);
        }
        else{
            test.setStatus(TestStatus.DELETED);
        }

        testRepository.save(test);
    }
    // ════════════════════════════════════════════════════════════════════════
    // SUBMISSION
    // ════════════════════════════════════════════════════════════════════════
    public List<SubmissionResponse> getCompletedSubmissionsByUser(String userId) {
        return submissionRepository.findByUserIdAndStatus(userId, SubmissionStatus.COMPLETED)
                .stream().map(this::mapToSubmissionResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public SubmissionResponse startSubmission(StartSubmissionRequest request) {

        CommunicationTest test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if(test.getStatus() != TestStatus.ACTIVE){
            throw new RuntimeException("Test is not available");
        }

        CommunicationSubmission submission = CommunicationSubmission.builder()
                .test(test)
                .userId(request.getUserId())
                .status(SubmissionStatus.IN_PROGRESS)
                .build();

        CommunicationSubmission saved = submissionRepository.save(submission);

        return mapToSubmissionResponse(saved);
    }
    
    @Transactional
    public SubmissionResponse completeSubmission(Long submissionId) {
        CommunicationSubmission submission = submissionRepository.findByIdWithAnswers(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + submissionId));

        // Calculate overall score from all answer feedbacks
        BigDecimal overallScore = submission.getAnswers().stream()
                .map(a -> feedbackRepository.findByAnswerId(a.getId())
                        .map(CommunicationFeedback::getAiScore)
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(
                        Math.max(submission.getAnswers().size(), 1)), 2, RoundingMode.HALF_UP);

        submission.setStatus(SubmissionStatus.COMPLETED);
        submission.setCompletedAt(LocalDateTime.now());
        submission.setOverallScore(overallScore);

        return mapToSubmissionResponse(submissionRepository.save(submission));
    }

    public List<SubmissionResponse> getSubmissionsByUser(String userId) {
        return submissionRepository.findByUserId(userId)
                .stream().map(this::mapToSubmissionResponse).collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    // ANSWER — Full Pipeline: upload → AssemblyAI → GPT
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Step 1: User submits audio URL → store answer and kick off AssemblyAI job
     */
    @Transactional
    public AnswerResponse submitAnswer(SubmitAnswerRequest request) {
        CommunicationSubmission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        CommunicationQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        CommunicationAnswer answer = CommunicationAnswer.builder()
                .submission(submission)
                .question(question)
                .audioUrl(request.getAudioUrl())
                .assemblyaiJobId(request.getAssemblyaiJobId())
                .transcriptionStatus(TranscriptionStatus.PENDING)
                .build();

        CommunicationAnswer saved = answerRepository.save(answer);
        log.info("Saved answer ID: {} with AssemblyAI job: {}", saved.getId(), request.getAssemblyaiJobId());
        return mapToAnswerResponse(saved);
    }
    public List<SubmissionResponse> getCompletedSubmissionsByEmail(String email) {
        return submissionRepository.findByUserIdAndStatus(email, SubmissionStatus.COMPLETED)
                .stream().map(this::mapToSubmissionResponse).collect(Collectors.toList());
    }

    public List<SubmissionResponse> getSubmissionsByEmail(String email) {
        return submissionRepository.findByUserId(email)
                .stream().map(this::mapToSubmissionResponse).collect(Collectors.toList());
    }
    /**
     * Step 2: Webhook / polling callback — AssemblyAI job completed
     *         Fetches transcript, then triggers GPT feedback generation
     */
    @Transactional
    public FeedbackResponse processTranscriptionComplete(String assemblyaiJobId) {

        // STEP 1: Fetch transcript from AssemblyAI and update answer
        assemblyAIService.fetchAndUpdateTranscript(assemblyaiJobId);

        // STEP 2: Reload updated answer
        CommunicationAnswer answer = answerRepository
                .findByAssemblyaiJobId(assemblyaiJobId)
                .orElseThrow(() ->
                        new RuntimeException("Answer not found for job: " + assemblyaiJobId));

        if (answer.getTranscriptionStatus() != TranscriptionStatus.COMPLETED) {
            throw new RuntimeException(
                    "Transcription not completed yet for job: " + assemblyaiJobId);
        }

        // Prevent duplicate feedback generation
        if (answer.getFeedback() != null) {
            log.info("Feedback already exists for answer {}", answer.getId());
            return mapToFeedbackResponse(answer.getFeedback());
        }

        // STEP 3: Generate Gemini AI feedback
        CommunicationFeedback feedback =
                geminiService.generateFeedback(answer);

        // STEP 4: Link feedback to answer
        feedback.setAnswer(answer);
        answer.setFeedback(feedback);

        feedbackRepository.save(feedback);
        answerRepository.save(answer);

        log.info("Gemini feedback generated for answer ID {}", answer.getId());

        return mapToFeedbackResponse(feedback);
    }   
    /**
     * Step 3: Get feedback for a specific answer
     */
    public FeedbackResponse getFeedbackByAnswerId(Long answerId) {
        CommunicationFeedback feedback = feedbackRepository.findByAnswerId(answerId)
                .orElseThrow(() -> new RuntimeException("Feedback not found for answer: " + answerId));
        return mapToFeedbackResponse(feedback);
    }

    // ════════════════════════════════════════════════════════════════════════
    // MAPPERS
    // ════════════════════════════════════════════════════════════════════════

    private TestResponse mapToTestResponse(CommunicationTest test) {

        List<QuestionResponse> questions = test.getQuestions() == null
                ? List.of()
                : test.getQuestions()
                      .stream()
                      .map(this::mapToQuestionResponse)
                      .collect(Collectors.toList());

        return TestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .totalQuestions(test.getTotalQuestions())
                .status(test.getStatus())
                .createdAt(test.getCreatedAt())
                .questions(questions)
                .build();
    }

    private QuestionResponse mapToQuestionResponse(CommunicationQuestion q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .timeLimit(q.getTimeLimit())
                .questionOrder(q.getQuestionOrder())
                .difficultyLevel(q.getDifficultyLevel())
                .category(q.getCategory())
                .build();
    }

    private SubmissionResponse mapToSubmissionResponse(CommunicationSubmission s) {
        List<AnswerResponse> answers = s.getAnswers() == null ? List.of() :
                s.getAnswers().stream().map(this::mapToAnswerResponse).collect(Collectors.toList());

        return SubmissionResponse.builder()
                .id(s.getId())
                .testId(s.getTest().getId())
                .userId(s.getUserId())
                .startedAt(s.getStartedAt())
                .completedAt(s.getCompletedAt())
                .overallScore(s.getOverallScore())
                .status(s.getStatus())
                .answers(answers)
                .build();
    }

    private AnswerResponse mapToAnswerResponse(CommunicationAnswer a) {
        FeedbackResponse feedbackResponse = a.getFeedback() != null
                ? mapToFeedbackResponse(a.getFeedback()) : null;

        return AnswerResponse.builder()
                .id(a.getId())
                .questionId(a.getQuestion().getId())
                .audioUrl(a.getAudioUrl())
                .assemblyaiJobId(a.getAssemblyaiJobId())
                .transcriptionStatus(a.getTranscriptionStatus())
                .transcript(a.getTranscript())
                .confidenceScore(a.getConfidenceScore())
                .audioDurationSeconds(a.getAudioDurationSeconds())
                .createdAt(a.getCreatedAt())
                .feedback(feedbackResponse)
                .build();
    }

    private FeedbackResponse mapToFeedbackResponse(CommunicationFeedback f) {
        return FeedbackResponse.builder()
                .id(f.getId())
                .aiScore(f.getAiScore())
                .grammarScore(f.getGrammarScore())
                .clarityScore(f.getClarityScore())
                .fluencyScore(f.getFluencyScore())
                .strengths(f.getStrengths())
                .weaknesses(f.getWeaknesses())
                .suggestions(f.getSuggestions())
                .createdAt(f.getCreatedAt())
                .build();
    }

    public Map<String, String> uploadToAssemblyAI(MultipartFile file) {

        try {

            /* STEP 1 — Upload audio */

            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", assemblyApiKey);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpEntity<byte[]> request =
                    new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<Map> uploadResponse =
                    restTemplate.exchange(
                            "https://api.assemblyai.com/v2/upload",
                            HttpMethod.POST,
                            request,
                            Map.class
                    );

            String audioUrl =
                    (String) uploadResponse.getBody().get("upload_url");


            /* STEP 2 — Start transcription */

            Map<String, Object> body = new HashMap<>();

            body.put("audio_url", audioUrl);

            body.put("speech_models", List.of("universal-2"));

            String webhookUrl = webhookUrlService.getAssemblyWebhookUrl();
            body.put("webhook_url", webhookUrl);
            
            HttpHeaders headers2 = new HttpHeaders();
            headers2.set("authorization", assemblyApiKey);
            headers2.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request2 =
                    new HttpEntity<>(body, headers2);

            ResponseEntity<Map> transcriptResponse =
                    restTemplate.exchange(
                            "https://api.assemblyai.com/v2/transcript",
                            HttpMethod.POST,
                            request2,
                            Map.class
                    );

            String jobId =
                    (String) transcriptResponse.getBody().get("id");

            Map<String, String> result = new HashMap<>();
            result.put("audioUrl", audioUrl);
            result.put("jobId", jobId);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("AssemblyAI upload failed", e);
        }
    }

	public List<TestResponse> getAllTestsByUser(String email) {
		// TODO Auto-generated method stub
				 return testRepository.findByCreatedBy(email)
			                .stream().map(this::mapToTestResponse).collect(Collectors.toList());
			    
	}
}
