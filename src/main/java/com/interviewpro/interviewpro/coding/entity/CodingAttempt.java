package com.interviewpro.interviewpro.coding.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coding_attempts")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CodingAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attemptId;

    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private CodingTest codingTest;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}