package com.interviewpro.interviewpro.mcq.service;

import com.interviewpro.interviewpro.mcq.dto.request.*;
import com.interviewpro.interviewpro.mcq.dto.response.*;
import com.interviewpro.interviewpro.mcq.entity.*;
import com.interviewpro.interviewpro.mcq.enums.TestType;
import com.interviewpro.interviewpro.mcq.repository.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorService {

    @Autowired private McqTestRepository testRepo;
    @Autowired private McqQuestionRepository questionRepo;
    @Autowired private McqOptionRepository optionRepo;
    @Autowired private McqAttemptRepository attemptRepo;
    
    /* =====================================================
       ✅ CREATE TEST (Default Private)
    ===================================================== */
    @Transactional
    public TestResponse createTest(CreateTestRequest req, String creatorEmail) {

        McqTest test = McqTest.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .testType(req.getTestType())
                .durationMinutes(req.getDurationMinutes())
                .totalQuestions(req.getTotalQuestions())
                .createdBy(creatorEmail)
                .isPublic(false) // ✅ always private first
                .build();

        testRepo.save(test);

        return TestResponse.builder()
                .testId(test.getTestId())
                .build();
    }

    /* =====================================================
       ✅ CREATOR DASHBOARD TESTS
    ===================================================== */
    public List<TestListResponse> getCreatorTests(String creatorEmail) {

        return testRepo.findByCreatedByOrderByTestIdDesc(creatorEmail)
                .stream()
                .map(test -> TestListResponse.builder()
                        .testId(test.getTestId())
                        .title(test.getTitle())
                        .durationMinutes(test.getDurationMinutes())
                        .publicTest(test.isPublic())
                        .testType(test.getTestType().name()) // 🔥 FIX
                        .build()
                )
                .toList();
    }

    /* =====================================================
       ✅ ADD QUESTION (ONLY PRIVATE)
    ===================================================== */
    @Transactional
    public QuestionResponse addQuestion(Long testId, AddQuestionRequest req) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (test.isPublic()) {
            throw new RuntimeException("❌ Cannot add questions after publishing");
        }

        McqQuestion question = McqQuestion.builder()
                .test(test)
                .questionText(req.getQuestionText())
                .difficulty(req.getDifficulty())
                .build();

        questionRepo.save(question);

        return QuestionResponse.builder()
                .questionId(question.getQuestionId())
                .questionText(question.getQuestionText())
                .options(List.of())
                .build();
    }

    /* =====================================================
       ✅ ADD OPTIONS (ONLY PRIVATE)
    ===================================================== */
    @Transactional
    public List<OptionResponse> addOptions(
            Long questionId,
            List<AddOptionRequest> requests) {

        McqQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        McqTest test = question.getTest();

        if (test.isPublic()) {
            throw new RuntimeException("❌ Cannot add options after publishing");
        }

        List<McqOption> options = requests.stream()
                .map(r -> McqOption.builder()
                        .question(question)
                        .optionText(r.getOptionText())
                        .isCorrect(r.isCorrect())
                        .build())
                .toList();

        optionRepo.saveAll(options);

        return options.stream()
                .map(o -> OptionResponse.builder()
                        .optionId(o.getOptionId())
                        .optionText(o.getOptionText())
                        .build())
                .toList();
    }

    /* =====================================================
       ✅ VIEW TEST DETAILS (CREATOR)
    ===================================================== */
    public TestDetailsResponse getTestDetails(Long testId) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        return new TestDetailsResponse(
                test.getTestId(),
                test.getTitle(),
                test.getTotalQuestions(),
                test.getDurationMinutes()
        );
    }
    
    /* =====================================================
    ✅ show privous quetion on ui (CREATOR)
 ===================================================== */
    public PreviousQuestionResponse getPreviousQuestion(Long questionId) {

        McqQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        return PreviousQuestionResponse.builder()
                .questionId(question.getQuestionId())
                .questionText(question.getQuestionText())
                .difficulty(question.getDifficulty().name())
                .options(
                        question.getOptions().stream()
                                .map(opt -> PreviousOptionResponse.builder()
                                        .optionId(opt.getOptionId())
                                        .optionText(opt.getOptionText())
                                        .isCorrect(opt.isCorrect())
                                        .build())
                                .toList()
                )
                .build();
    }

    /* =====================================================
    ✅ update privous quetion (CREATOR)
 ===================================================== */
    @Transactional
    public QuestionResponse updateQuestion(Long questionId,
                                          UpdateQuestionRequest request) {

        McqQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        McqTest test = question.getTest();

        // ✅ RULE: Cannot update if test is PUBLIC
        if (test.isPublic()) {
            throw new RuntimeException("❌ Cannot update question after publishing");
        }

        // ✅ Update question fields
        question.setQuestionText(request.getQuestionText());
        question.setDifficulty(request.getDifficulty());

        // ✅ Replace all options safely
        question.getOptions().clear();

        List<McqOption> updatedOptions =
                request.getOptions().stream()
                        .map(opt -> McqOption.builder()
                                .question(question)
                                .optionText(opt.getOptionText())
                                .isCorrect(opt.isCorrect())
                                .build())
                        .toList();

        question.getOptions().addAll(updatedOptions);

        questionRepo.save(question);

        return QuestionResponse.builder()
                .questionId(question.getQuestionId())
                .questionText(question.getQuestionText())
                .options(
                        updatedOptions.stream()
                                .map(o -> OptionResponse.builder()
                                        .optionId(o.getOptionId())
                                        .optionText(o.getOptionText())
                                        .build())
                                .toList()
                )
                .build();
    }
    
    /* =====================================================
    ✅ GET QUESTION BY ID (CREATOR)
 ===================================================== */
    public List<Long> getQuestionIds(Long testId) {
        return questionRepo.findByTest_TestId(testId)
                .stream()
                .map(McqQuestion::getQuestionId)
                .toList();
    }

    
    /* =====================================================
       ✅ FULL TEST VIEW (CREATOR)
    ===================================================== */
    @Transactional
    public TestViewResponse viewTestDetails(Long testId) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        List<QuestionWithAnswerResponse> questions =
                test.getQuestions().stream()
                        .map(q -> QuestionWithAnswerResponse.builder()
                                .questionId(q.getQuestionId())
                                .questionText(q.getQuestionText())
                                .options(
                                        q.getOptions().stream()
                                                .map(o -> OptionWithAnswerResponse.builder()
                                                        .optionId(o.getOptionId())
                                                        .optionText(o.getOptionText())
                                                        .isCorrect(o.isCorrect())
                                                        .build())
                                                .toList()
                                )
                                .build())
                        .toList();

        return TestViewResponse.builder()
                .testId(test.getTestId())
                .title(test.getTitle())
                .attempted(attemptRepo.existsByTest_TestId(testId))
                .questions(questions)
                .build();
    }

    /* =====================================================
       ✅ UPDATE FULL TEST (ONLY PRIVATE)
    ===================================================== */
    @Transactional
    public void updateFullTest(Long testId, UpdateFullTestRequest req) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (test.isPublic()) {
            throw new RuntimeException("❌ Public test cannot be updated");
        }

        // ✅ Meta update
        test.setTitle(req.getTitle());
        test.setDurationMinutes(req.getDurationMinutes());

        // ✅ Replace all questions
        test.getQuestions().clear();

        for (UpdateQuestionResponse qdto : req.getQuestions()) {

            McqQuestion question = McqQuestion.builder()
                    .questionText(qdto.getQuestionText())
                    .test(test)
                    .build();

            List<McqOption> options =
                    qdto.getOptions().stream()
                            .map(odto -> McqOption.builder()
                                    .optionText(odto.getOptionText())
                                    .isCorrect(odto.isCorrect())
                                    .question(question)
                                    .build())
                            .toList();

            question.setOptions(options);
            test.getQuestions().add(question);
        }

        testRepo.save(test);
    }

    //load test for edit
    @Transactional
    public UpdateFullTestRequest loadTestForEdit(Long testId) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // ✅ Block if Public
        if (test.isPublic()) {
            throw new RuntimeException("❌ Public tests cannot be edited");
        }

        UpdateFullTestRequest response = new UpdateFullTestRequest();

        response.setTitle(test.getTitle());
        response.setDurationMinutes(test.getDurationMinutes());

        // ✅ Load Questions + Options
        List<UpdateQuestionResponse> questionList =
                test.getQuestions().stream().map(q -> {

                    UpdateQuestionResponse qdto = new UpdateQuestionResponse();

                    qdto.setQuestionId(q.getQuestionId());
                    qdto.setQuestionText(q.getQuestionText());
                    qdto.setDifficulty(q.getDifficulty());

                    // ✅ Load Options
                    List<UpdateOptionResponse> optionList =
                            q.getOptions().stream().map(o -> {

                                UpdateOptionResponse odto =
                                        new UpdateOptionResponse();

                                odto.setOptionId(o.getOptionId());
                                odto.setOptionText(o.getOptionText());
                                odto.setCorrect(o.isCorrect());

                                return odto;

                            }).toList();

                    qdto.setOptions(optionList);

                    return qdto;

                }).toList();

        response.setQuestions(questionList);

        return response;
    }

//update private test
    @Transactional
    public void updatePrivateTest(Long testId, UpdateFullTestRequest req) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // ✅ Rule: Block Public Test Updates
        if (test.isPublic()) {
            throw new RuntimeException("❌ Public tests cannot be updated");
        }

        // ✅ Update Test Meta
        test.setTitle(req.getTitle());
        test.setDurationMinutes(req.getDurationMinutes());

        // ✅ Remove Old Questions Completely
        test.getQuestions().clear();

        // ✅ Insert New Updated Questions
        for (UpdateQuestionResponse qdto : req.getQuestions()) {

            McqQuestion question = McqQuestion.builder()
                    .questionText(qdto.getQuestionText())
                    .difficulty(qdto.getDifficulty())
                    .test(test)
                    .build();

            // ✅ Insert Options
            List<McqOption> options =
                    qdto.getOptions().stream()
                            .map(odto -> McqOption.builder()
                                    .optionText(odto.getOptionText())
                                    .isCorrect(odto.isCorrect())
                                    .question(question)
                                    .build())
                            .toList();

            question.setOptions(options);

            test.getQuestions().add(question);
        }

        testRepo.save(test);
    }

    
    /* =====================================================
       ✅ PUBLISH TEST (LOCK FOREVER)
    ===================================================== */
    @Transactional
    public void publishTest(Long testId) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (test.isPublic()) {
            throw new RuntimeException("❌ Test already published");
        }

        test.setPublic(true);
        testRepo.save(test);
    }

    /* =====================================================
       ✅ DELETE TEST (ALWAYS ALLOWED)
    ===================================================== */
    @Transactional
    public void deleteFullTest(Long testId) {

        McqTest test = testRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        testRepo.delete(test);
    }



    /* =====================================================
    ✅ CREATOR DASHBOARD TESTS BY TYPE
 ===================================================== */
    public List<TestListResponse> getTestsByCreatorAndType(
            String creatorEmail,
            String type
    ) {
    	System.out.println("\n------------------------"+type+"--------"+creatorEmail+"\n");
        TestType testType;
        try {
            testType = TestType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid test type: " + type);
        }


        return testRepo
                .findByTestTypeAndCreatedBy(testType, creatorEmail)
                .stream()
                .map(test -> TestListResponse.builder()
                        .testId(test.getTestId())
                        .title(test.getTitle())
                        .durationMinutes(test.getDurationMinutes())
                        .publicTest(test.isPublic())
                        .testType(test.getTestType().name())
                        .build()
                )
                .toList();
    }


  }
