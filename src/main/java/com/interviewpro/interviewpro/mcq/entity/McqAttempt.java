package com.interviewpro.interviewpro.mcq.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aptitude_attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private McqTest test;

    // STUDENT email
    @Column(nullable = false)
    private String studentEmail;

    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    private int score;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<McqAnswer> answers;
}
