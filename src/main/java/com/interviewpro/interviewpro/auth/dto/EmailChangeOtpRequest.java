package com.interviewpro.interviewpro.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailChangeOtpRequest {
@NotBlank
@Email
private String newEmail;


public String getNewEmail() { return newEmail; }
public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
}