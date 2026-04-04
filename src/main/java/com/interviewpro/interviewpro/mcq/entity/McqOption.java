package com.interviewpro.interviewpro.mcq.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aptitude_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private McqQuestion question;

    @Column(nullable = false, length = 255)
    private String optionText;

    @Column(nullable = false)
    private boolean isCorrect;
}
