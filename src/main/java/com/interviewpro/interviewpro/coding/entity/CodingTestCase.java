package com.interviewpro.interviewpro.coding.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coding_test_cases")
@Getter @Setter
public class CodingTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Many TestCases → One Question
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_question_id")
    private CodingQuestion codingQuestion;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String input;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    private Boolean sample;
}
