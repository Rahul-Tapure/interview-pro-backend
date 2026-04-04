package com.interviewpro.interviewpro.coding.entity;

import com.interviewpro.interviewpro.mcq.enums.TestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coding_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false, length = 20)
    private TestType testType;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private boolean publicTest = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ Correct mappedBy field name
    @OneToMany(
        mappedBy = "codingTest",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<CodingQuestion> questions = new ArrayList<>();
}
