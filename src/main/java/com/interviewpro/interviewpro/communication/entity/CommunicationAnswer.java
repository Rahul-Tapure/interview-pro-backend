package com.interviewpro.interviewpro.communication.entity;

import com.interviewpro.interviewpro.communication.enums.TranscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "communication_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private CommunicationSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private CommunicationQuestion question;

    // ── AssemblyAI Fields ──────────────────────────────────────────────────────

    @Column(name = "audio_url", columnDefinition = "TEXT")
    private String audioUrl;

    @Column(name = "assemblyai_job_id", length = 100)
    private String assemblyaiJobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transcription_status")
    @Builder.Default
    private TranscriptionStatus transcriptionStatus = TranscriptionStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore;

    @Column(name = "audio_duration_seconds")
    private Integer audioDurationSeconds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "answer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CommunicationFeedback feedback;
}
