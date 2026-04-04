package com.interviewpro.interviewpro.communication.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewpro.interviewpro.communication.entity.CommunicationAnswer;
import com.interviewpro.interviewpro.communication.enums.TranscriptionStatus;
import com.interviewpro.interviewpro.communication.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssemblyAIService {

    @Value("${assemblyai.api.key}")
    private String apiKey;

    private static final String ASSEMBLY_AI_BASE_URL = "https://api.assemblyai.com/v2";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CommunicationAnswerRepository answerRepository;

    // ── Submit audio URL to AssemblyAI ────────────────────────────────────────

    public String submitTranscriptionJob(String audioUrl) {
        HttpHeaders headers = buildHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("audio_url", audioUrl);
        body.put("punctuate", true);
        body.put("format_text", true);
        body.put("language_detection", true);
        body.put("disfluencies", true);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    ASSEMBLY_AI_BASE_URL + "/transcript", request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            String jobId = json.get("id").asText();
            log.info("AssemblyAI job submitted. Job ID: {}", jobId);
            return jobId;

        } catch (Exception e) {
            log.error("Failed to submit AssemblyAI job for audio: {}", audioUrl, e);
            throw new RuntimeException("AssemblyAI submission failed: " + e.getMessage());
        }
    }

    // ── Poll job status ───────────────────────────────────────────────────────

    public TranscriptionStatus pollJobStatus(String jobId) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ASSEMBLY_AI_BASE_URL + "/transcript/" + jobId,
                    HttpMethod.GET, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            String status = json.get("status").asText();

            return switch (status) {
                case "completed" -> TranscriptionStatus.COMPLETED;
                case "error"     -> TranscriptionStatus.FAILED;
                case "processing" -> TranscriptionStatus.PROCESSING;
                default           -> TranscriptionStatus.PENDING;
            };

        } catch (Exception e) {
            log.error("Failed to poll AssemblyAI job: {}", jobId, e);
            return TranscriptionStatus.FAILED;
        }
    }

    // ── Fetch completed transcript and update answer ───────────────────────────

    public void fetchAndUpdateTranscript(String jobId) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ASSEMBLY_AI_BASE_URL + "/transcript/" + jobId,
                    HttpMethod.GET, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());

            if (!"completed".equals(json.get("status").asText())) {
                log.warn("AssemblyAI job {} is not yet completed", jobId);
                return;
            }

            String transcript         = json.get("text").asText();
            double confidence         = json.get("confidence").asDouble();
            int    audioDurationSecs  = json.get("audio_duration").asInt();

            // Update the answer entity
            answerRepository.findByAssemblyaiJobId(jobId).ifPresent(answer -> {
                answer.setTranscript(transcript);
                answer.setConfidenceScore(BigDecimal.valueOf(confidence));
                answer.setAudioDurationSeconds(audioDurationSecs);
                answer.setTranscriptionStatus(TranscriptionStatus.COMPLETED);
                answerRepository.save(answer);
                log.info("Transcript saved for answer ID: {}", answer.getId());
            });

        } catch (Exception e) {
            log.error("Failed to fetch AssemblyAI transcript for job: {}", jobId, e);
            answerRepository.findByAssemblyaiJobId(jobId).ifPresent(answer -> {
                answer.setTranscriptionStatus(TranscriptionStatus.FAILED);
                answerRepository.save(answer);
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
