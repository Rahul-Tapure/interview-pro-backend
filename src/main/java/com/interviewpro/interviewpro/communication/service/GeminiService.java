package com.interviewpro.interviewpro.communication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.interviewpro.interviewpro.communication.dto.response.FeedbackResponse;
import com.interviewpro.interviewpro.communication.entity.*;
import com.interviewpro.interviewpro.communication.repository.CommunicationFeedbackRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final CommunicationFeedbackRepository feedbackRepository;

    @Value("${communication.prompt}")
    private String communicationPrompt;

    public CommunicationFeedback generateFeedback(CommunicationAnswer answer) {

        try {

            String question = answer.getQuestion().getQuestionText();
            String transcript = answer.getTranscript();

            Content content = Content.builder()
                    .parts(
                            Part.fromText("Question: " + question),
                            Part.fromText("Answer: " + transcript),
                            Part.fromText(communicationPrompt)
                    )
                    .build();

            GenerateContentResponse response =
                    geminiClient.models.generateContent(
                            "gemini-2.5-flash",
                            content,
                            GenerateContentConfig.builder()
                                    .temperature(0.0f)
                                    .build()
                    );

            String json = cleanJson(response.text());

            FeedbackResponse dto =
                    objectMapper.readValue(json, FeedbackResponse.class);

            CommunicationFeedback feedback = CommunicationFeedback.builder()
                    .answer(answer)
                    .aiScore(dto.getAiScore())
                    .grammarScore(dto.getGrammarScore())
                    .clarityScore(dto.getClarityScore())
                    .fluencyScore(dto.getFluencyScore())
                    .strengths(dto.getStrengths())
                    .weaknesses(dto.getWeaknesses())
                    .suggestions(dto.getSuggestions())
                    .rawGptResponse(json)
                    .build();

            return feedbackRepository.save(feedback);

        } catch (Exception e) {
            throw new RuntimeException("Gemini analysis failed", e);
        }
    }

    private String cleanJson(String raw) {

        if (raw.startsWith("```")) {

            int firstBrace = raw.indexOf("{");
            int lastBrace = raw.lastIndexOf("}");

            return raw.substring(firstBrace, lastBrace + 1);
        }

        return raw;
    }
}