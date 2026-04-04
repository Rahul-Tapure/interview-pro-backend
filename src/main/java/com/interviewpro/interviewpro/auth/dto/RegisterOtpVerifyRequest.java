package com.interviewpro.interviewpro.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterOtpVerifyRequest {

    @Email
    private String email;

    @NotBlank
    private String otp;
}