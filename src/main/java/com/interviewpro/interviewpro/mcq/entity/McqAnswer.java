package com.interviewpro.interviewpro.mcq.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aptitude_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private McqAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private McqQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private McqOption selectedOption;

    private boolean isCorrect;
}
