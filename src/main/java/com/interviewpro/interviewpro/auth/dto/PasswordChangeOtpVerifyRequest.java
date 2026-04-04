package com.interviewpro.interviewpro.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeOtpVerifyRequest {
@NotBlank
@Email
private String email;


@NotBlank
@Size(min = 4, max = 8)
private String otp;

@NotBlank
@Size(min = 8, max = 100)
private String newPassword;

public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getOtp() { return otp; }
public void setOtp(String otp) { this.otp = otp; }
public String getNewPassword() { return newPassword; }
public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}