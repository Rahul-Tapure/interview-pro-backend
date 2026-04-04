package com.interviewpro.interviewpro.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "otp_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;             // current user's email

    @Column(nullable = false)
    private String hashedOtp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType type;

    private String targetEmail;       // only used for EMAIL_CHANGE

    @Column(nullable = false)
    private Instant expiresAt;

    @Builder.Default
    private int attempts = 0;

    @Builder.Default
    private boolean used = false;
}