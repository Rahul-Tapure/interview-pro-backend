package com.interviewpro.interviewpro.judge0.entity;

import java.time.LocalDateTime;

import com.interviewpro.interviewpro.coding.entity.CodingQuestion;
import com.interviewpro.interviewpro.coding.entity.CodingTest;
import com.interviewpro.interviewpro.judge0.enums.SubmissionStatus;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(
    indexes = {
        @Index(name = "idx_question", columnList = "questionId"),
        @Index(name = "idx_user", columnList = "userEmail"),
        @Index(name = "idx_test", columnList = "test_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private String userEmail;
    
 // Option: Add relation to CodingSubmission entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId", insertable = false, updatable = false)
    private CodingQuestion codingQuestion;
    
    @Column(name = "attempt_id")
    private String attemptId;  // UUID generated per attempt session

    // ✅ RELATION WITH CODING TEST
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private CodingTest codingTest;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String sourceCode;

    private Integer languageId;

    private Integer totalTestCases;
    private Integer passedTestCases;
    private Integer score;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    private Double executionTime;
    private Integer memoryUsed;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorOutput;

    private LocalDateTime submittedAt;
}
