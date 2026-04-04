package com.interviewpro.interviewpro.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_otp_codes")
public class AuthOtpCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String ownerEmail;
	private String purpose;
	private String otpHash;
	private String targetValue;
	private Integer attempts;
	private Boolean consumed;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	public Long getId() { return id; }
	public String getOwnerEmail() { return ownerEmail; }
	public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
	public String getPurpose() { return purpose; }
	public void setPurpose(String purpose) { this.purpose = purpose; }
	public String getOtpHash() { return otpHash; }
	public void setOtpHash(String otpHash) { this.otpHash = otpHash; }
	public String getTargetValue() { return targetValue; }
	public void setTargetValue(String targetValue) { this.targetValue = targetValue; }
	public Integer getAttempts() { return attempts; }
	public void setAttempts(Integer attempts) { this.attempts = attempts; }
	public Boolean getConsumed() { return consumed; }
	public void setConsumed(Boolean consumed) { this.consumed = consumed; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public LocalDateTime getExpiresAt() { return expiresAt; }
	public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
