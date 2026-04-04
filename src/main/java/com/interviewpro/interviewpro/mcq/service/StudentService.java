package com.interviewpro.interviewpro.mcq.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.mcq.dto.request.SubmitTestRequest;
import com.interviewpro.interviewpro.mcq.dto.response.OptionResponse;
import com.interviewpro.interviewpro.mcq.dto.response.QuestionResponse;
import com.interviewpro.interviewpro.mcq.dto.response.ResultResponse;
import com.interviewpro.interviewpro.mcq.dto.response.StartTestResponse;

import com.interviewpro.interviewpro.mcq.entity.McqAnswer;
import com.interviewpro.interviewpro.mcq.entity.McqAttempt;
import com.interviewpro.interviewpro.mcq.entity.McqOption;
import com.interviewpro.interviewpro.mcq.entity.McqTest;
import com.interviewpro.interviewpro.mcq.repository.McqAnswerRepository;
import com.interviewpro.interviewpro.mcq.repository.McqAttemptRepository;
import com.interviewpro.interviewpro.mcq.repository.McqOptionRepository;
import com.interviewpro.interviewpro.mcq.repository.McqTestRepository;

import jakarta.transaction.Transactional;


@Service
public class StudentService {
    @Autowired private McqTestRepository testRepo;
    @Autowired private McqOptionRepository optionRepo;
    @Autowired private McqAttemptRepository attemptRepo;
    @Autowired private McqAnswerRepository answerRepo;

	  /* =====================================================
    ✅ STUDENT: LIST ONLY PUBLIC TESTS
 ===================================================== */

 /* =====================================================
    ✅ STUDENT: START TEST ONLY IF PUBLIC
 ===================================================== */
 @Transactional
 public StartTestResponse startTest(Long testId, String studentEmail) {

     McqTest test = testRepo.findById(testId)
             .orElseThrow(() -> new RuntimeException("Test not found"));

     if (!test.isPublic()) {
         throw new RuntimeException("❌ This test is private");
     }

     McqAttempt attempt = McqAttempt.builder()
             .test(test)
             .studentEmail(studentEmail)
             .startTime(LocalDateTime.now())
             .score(0)
             .build();

     attemptRepo.save(attempt);

     List<QuestionResponse> questions =
             test.getQuestions().stream()
                     .map(q -> QuestionResponse.builder()
                             .questionId(q.getQuestionId())
                             .questionText(q.getQuestionText())
                             .options(
                                     q.getOptions().stream()
                                             .map(o -> OptionResponse.builder()
                                                     .optionId(o.getOptionId())
                                                     .optionText(o.getOptionText())
                                                     .build())
                                             .toList())
                             .build())
                     .toList();

     return StartTestResponse.builder()
    		 .testName(test.getTitle())
             .attemptId(attempt.getAttemptId())
             .testId(test.getTestId())
             .durationMinutes(test.getDurationMinutes())
             .questions(questions)
             .build();
 }

 /* =====================================================
    ✅ SUBMIT TEST
 ===================================================== */
 @Transactional
 public ResultResponse submitTest(Long attemptId, SubmitTestRequest req) {

     McqAttempt attempt = attemptRepo.findById(attemptId)
             .orElseThrow(() -> new RuntimeException("Attempt not found"));

     int correct = 0;

     for (Map.Entry<Long, Long> entry : req.getAnswers().entrySet()) {

         McqOption option = optionRepo.findById(entry.getValue())
                 .orElseThrow(() -> new RuntimeException("Option not found"));

         boolean isCorrect = option.isCorrect();
         if (isCorrect) correct++;

         McqAnswer answer = McqAnswer.builder()
                 .attempt(attempt)
                 .question(option.getQuestion())
                 .selectedOption(option)
                 .isCorrect(isCorrect)
                 .build();

         answerRepo.save(answer);
     }

     int totalQuestions = attempt.getTest().getQuestions().size();
     int wrong = req.getAnswers().size() - correct;
     int unattempted = totalQuestions - req.getAnswers().size();

     attempt.setScore(correct);
     attempt.setEndTime(LocalDateTime.now());
     attemptRepo.save(attempt);

     Duration duration =
             Duration.between(attempt.getStartTime(), attempt.getEndTime());

     String timeTaken = String.format(
             "%02d:%02d:%02d",
             duration.toHours(),
             duration.toMinutesPart(),
             duration.toSecondsPart()
     );

     boolean passed = correct >= Math.ceil(totalQuestions * 0.4);

     return ResultResponse.builder()
             .score(correct)
             .totalQuestions(totalQuestions)
             .correct(correct)
             .wrong(wrong)
             .unattempted(unattempted)
             .timeTaken(timeTaken)
             .passed(passed)
             .message("✅ Test submitted successfully")
             .build();
 }

 /* =====================================================
    ✅ STUDENT RESULTS
 ===================================================== */
 public List<ResultResponse> getStudentResults(String email) {

	    return attemptRepo.findByStudentEmail(email)
	            .stream()
	            .map(a -> {

	                int totalQuestions = a.getTest().getQuestions().size();

	                Duration duration = Duration.ZERO;

	                if (a.getStartTime() != null && a.getEndTime() != null) {
	                    duration = Duration.between(
	                            a.getStartTime(),
	                            a.getEndTime()
	                    );
	                }

	                String timeTaken = String.format(
	                        "%02d:%02d:%02d",
	                        duration.toHours(),
	                        duration.toMinutesPart(),
	                        duration.toSecondsPart()
	                );

	                return ResultResponse.builder()
	                        // ✅ NEW DATA
	                        .title(a.getTest().getTitle())
	                        .type(a.getTest().getTestType().name())

	                        // existing
	                        .score(a.getScore())
	                        .totalQuestions(totalQuestions)
	                        .correct(a.getScore())
	                        .wrong(totalQuestions - a.getScore())
	                        .unattempted(0)
	                        .timeTaken(timeTaken)
	                        .passed(a.getScore() >= Math.ceil(totalQuestions * 0.4))
	                        .message("Completed")
	                        .build();
	            })
	            .toList();
	}

}
