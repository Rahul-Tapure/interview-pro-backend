package com.interviewpro.interviewpro.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmailChangeOtpVerifyRequest {
@NotBlank
@Email
private String newEmail;

@NotBlank
@Size(min = 4, max = 8)
private String otp;

public String getNewEmail() { return newEmail; }
public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
public String getOtp() { return otp; }
public void setOtp(String otp) { this.otp = otp; }
}