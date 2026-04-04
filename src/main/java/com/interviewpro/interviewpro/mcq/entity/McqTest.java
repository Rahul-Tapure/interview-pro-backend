package com.interviewpro.interviewpro.mcq.entity;

import com.interviewpro.interviewpro.mcq.enums.TestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mcq_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false, length = 20)
    private TestType testType;   // ✅ KEY CHANGE

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private boolean isPublic = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<McqQuestion> questions;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<McqAttempt> attempts;
}
