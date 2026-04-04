package com.interviewpro.interviewpro.resume.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String role;   // ✅ NEW FIELD

    private int score;
    private int atsOptimizationScore;

    @Column(length = 5000)
    private String pros;

    @Column(length = 5000)
    private String cons;

    @Column(length = 5000)
    private String suggestions;

    private LocalDateTime createdAt;
}