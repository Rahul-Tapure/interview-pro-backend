package com.interviewpro.interviewpro.resume.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.interviewpro.interviewpro.resume.dto.ResumeAnalysisResponseDto;
import com.interviewpro.interviewpro.resume.dto.ResumeResultDto;
import com.interviewpro.interviewpro.resume.entity.ResumeAnalysis;
import com.interviewpro.interviewpro.resume.repository.ResumeRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;
    private final Client geminiClient;

    @Value("${resume.prompt}")
    private String resumePrompt;

    public ResponseEntity<?> analyzeResume(String roles, MultipartFile file) throws Exception {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Extract text
        Tika tika = new Tika();
        String extracted = tika.parseToString(new ByteArrayInputStream(file.getBytes()));

        // Prepare AI Content
        Content content = Content.builder()
                .parts(
                        Part.fromText(extracted),
                        Part.fromText(resumePrompt + " Analyze for role: " + roles)
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

        String resultJson = cleanJson(response.text());

        ResumeResultDto dto =
                objectMapper.readValue(resultJson, ResumeResultDto.class);

        if (dto.getScore() == 0) {
            return ResponseEntity.badRequest().body("Invalid resume for selected role");
        }

        ResumeAnalysis entity = ResumeAnalysis.builder()
                .username(username)
                .role(roles)   // ✅ SAVE ROLE
                .score(dto.getScore())
                .atsOptimizationScore(dto.getAtsOptimizationScore())
                .pros(objectMapper.writeValueAsString(dto.getPros()))
                .cons(objectMapper.writeValueAsString(dto.getCons()))
                .suggestions(objectMapper.writeValueAsString(dto.getSuggestions()))
                .createdAt(LocalDateTime.now())
                .build();

        ResumeAnalysis saved = resumeRepository.save(entity);

        ResumeAnalysisResponseDto responseDto =
                new ResumeAnalysisResponseDto(
                        saved.getId(),
                        saved.getRole(),   // ✅ return role
                        dto.getScore(),
                        dto.getAtsOptimizationScore(),
                        dto.getPros(),
                        dto.getCons(),
                        dto.getSuggestions()
                );
        return ResponseEntity.ok(responseDto);
    }

    private String cleanJson(String raw) {
        if (raw.startsWith("```")) {
            int firstBrace = raw.indexOf("{");
            int lastBrace = raw.lastIndexOf("}");
            return raw.substring(firstBrace, lastBrace + 1);
        }
        return raw;
    }
    
    public ResponseEntity<?> getResumeById(Long id) throws Exception {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return resumeRepository.findById(id)
                .filter(resume -> resume.getUsername().equals(username))
                .map(entity -> {
                    try {
                        ResumeResultDto dto = new ResumeResultDto(
                                entity.getScore(),
                                entity.getAtsOptimizationScore(),
                                objectMapper.readValue(entity.getPros(), java.util.List.class),
                                objectMapper.readValue(entity.getCons(), java.util.List.class),
                                objectMapper.readValue(entity.getSuggestions(), java.util.List.class)
                        );
                        return ResponseEntity.ok(dto);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().body("Error parsing resume data");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    public ResponseEntity<?> getUserResumes() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                resumeRepository.findByUsernameOrderByCreatedAtDesc(username)
        );
    }
    
    public ResponseEntity<?> getLastReport() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return resumeRepository
                .findTopByUsernameOrderByCreatedAtDesc(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    public ResponseEntity<?> deleteResume(Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return resumeRepository.findById(id)
                .filter(resume -> resume.getUsername().equals(username))
                .map(entity -> {
                    resumeRepository.delete(entity);
                    return ResponseEntity.ok("Resume deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    public ResponseEntity<byte[]> downloadReport(Long id) throws Exception {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ResumeAnalysis resume = resumeRepository.findById(id)
                .filter(r -> r.getUsername().equals(username))
                .orElseThrow(() -> new RuntimeException("Report not found"));

        byte[] pdf = generatePdfReport(resume);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resume-analysis-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    public byte[] generatePdfReport(ResumeAnalysis resume) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        document.add(new Paragraph("InterviewPro Resume Analysis Report"));
        document.add(new Paragraph("--------------------------------------------------"));
        document.add(new Paragraph("Username: " + resume.getUsername()));
        document.add(new Paragraph("Role: " + resume.getRole()));
        document.add(new Paragraph("Score: " + resume.getScore()));
        document.add(new Paragraph("ATS Score: " + resume.getAtsOptimizationScore()));
        document.add(new Paragraph(" "));

        // Convert JSON to List
        java.util.List<String> pros =
                objectMapper.readValue(resume.getPros(), java.util.List.class);

        java.util.List<String> cons =
                objectMapper.readValue(resume.getCons(), java.util.List.class);

        java.util.List<String> suggestions =
                objectMapper.readValue(resume.getSuggestions(), java.util.List.class);

        document.add(new Paragraph("Pros:"));
        for (String p : pros) {
            document.add(new Paragraph("• " + p));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Cons:"));
        for (String c : cons) {
            document.add(new Paragraph("• " + c));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Suggestions:"));
        for (String s : suggestions) {
            document.add(new Paragraph("• " + s));
        }

        document.close();

        return out.toByteArray();
    }    
}