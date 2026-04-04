package com.interviewpro.interviewpro.coding.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coding_questions")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CodingQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    private Integer timeLimit;
    private Integer memoryLimit;

    private Boolean published = false;

    private String createdBy;

    // ✅ NEW FIELD: Step Number (1,2,3...)
    private Integer questionIndex;

    // ✅ Many Questions → One Test
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private CodingTest codingTest;

    // ✅ 1 Question → Many TestCases
    @OneToMany(
        mappedBy = "codingQuestion",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<CodingTestCase> testCases = new ArrayList<>();
}

