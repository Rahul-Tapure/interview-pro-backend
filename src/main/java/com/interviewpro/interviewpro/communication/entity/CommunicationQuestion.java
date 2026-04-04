package com.interviewpro.interviewpro.communication.entity;

import com.interviewpro.interviewpro.communication.enums.DifficultyLevel;
import com.interviewpro.interviewpro.communication.enums.QuestionCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communication_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private CommunicationTest test;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "time_limit")
    private Integer timeLimit; // in seconds

    @Column(name = "question_order")
    private Integer questionOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 100)
    private QuestionCategory category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommunicationAnswer> answers = new ArrayList<>();
}
