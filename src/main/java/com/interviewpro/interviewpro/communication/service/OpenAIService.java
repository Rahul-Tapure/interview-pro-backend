package com.interviewpro.interviewpro.communication.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewpro.interviewpro.communication.entity.CommunicationAnswer;
import com.interviewpro.interviewpro.communication.entity.CommunicationFeedback;
import com.interviewpro.interviewpro.communication.repository.CommunicationFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o}")
    private String model;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CommunicationFeedbackRepository feedbackRepository;

    // ── Generate GPT Feedback from transcript ─────────────────────────────────

    public CommunicationFeedback generateFeedback(CommunicationAnswer answer) {
        String transcript   = answer.getTranscript();
        String questionText = answer.getQuestion().getQuestionText();

        String prompt = buildPrompt(questionText, transcript);
        String rawGptResponse = callOpenAI(prompt);

        return parseFeedbackAndSave(answer, rawGptResponse);
    }

    // ── Build prompt ──────────────────────────────────────────────────────────

    private String buildPrompt(String question, String transcript) {
        return """
            You are an expert communication coach evaluating a candidate's spoken response.

            Interview Question: "%s"
            Candidate's Response (transcribed): "%s"

            Evaluate the response and respond ONLY with a valid JSON object in this exact format:
            {
              "ai_score": <0-100>,
              "grammar_score": <0-100>,
              "clarity_score": <0-100>,
              "fluency_score": <0-100>,
              "strengths": "<what was done well>",
              "weaknesses": "<areas that need improvement>",
              "suggestions": "<specific actionable suggestions>"
            }
            """.formatted(question, transcript);
    }

    // ── Call OpenAI API ───────────────────────────────────────────────────────

    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(message));
        body.put("temperature", 0.3);
        body.put("max_tokens", 600);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_URL, request, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.at("/choices/0/message/content").asText();

        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
            throw new RuntimeException("OpenAI call failed: " + e.getMessage());
        }
    }

    // ── Parse GPT JSON → CommunicationFeedback ────────────────────────────────

    private CommunicationFeedback parseFeedbackAndSave(CommunicationAnswer answer, String rawGptResponse) {
        try {
            // Strip possible markdown code fences
            String cleanJson = rawGptResponse
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode node = objectMapper.readTree(cleanJson);

            CommunicationFeedback feedback = CommunicationFeedback.builder()
                    .answer(answer)
                    .aiScore(BigDecimal.valueOf(node.get("ai_score").asDouble()))
                    .grammarScore(BigDecimal.valueOf(node.get("grammar_score").asDouble()))
                    .clarityScore(BigDecimal.valueOf(node.get("clarity_score").asDouble()))
                    .fluencyScore(BigDecimal.valueOf(node.get("fluency_score").asDouble()))
                    .strengths(node.get("strengths").asText())
                    .weaknesses(node.get("weaknesses").asText())
                    .suggestions(node.get("suggestions").asText())
                    .rawGptResponse(rawGptResponse)
                    .build();

            return feedbackRepository.save(feedback);

        } catch (Exception e) {
            log.error("Failed to parse GPT feedback response: {}", rawGptResponse, e);
            throw new RuntimeException("Failed to parse GPT feedback: " + e.getMessage());
        }
    }
}
