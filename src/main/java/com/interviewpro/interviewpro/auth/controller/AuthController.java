package com.interviewpro.interviewpro.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewpro.interviewpro.auth.dto.EmailChangeOtpRequest;
import com.interviewpro.interviewpro.auth.dto.EmailChangeOtpVerifyRequest;
import com.interviewpro.interviewpro.auth.dto.PasswordChangeOtpRequest;
import com.interviewpro.interviewpro.auth.dto.PasswordChangeOtpVerifyRequest;
import com.interviewpro.interviewpro.auth.dto.RegisterOtpVerifyRequest;
import com.interviewpro.interviewpro.auth.entity.UserLogin;
import com.interviewpro.interviewpro.auth.entity.UserRegister;
import com.interviewpro.interviewpro.auth.service.AuthServices;
import com.interviewpro.interviewpro.mcq.dto.response.UserInfoResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("interviewpro/entry/v1")
public class AuthController {
	
	@Autowired
	private AuthServices authServices;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody UserRegister req) {
	    return authServices.register(req);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody UserLogin req) {
	    return authServices.login(req);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
	    return authServices.logout();
	}

	@PostMapping("/deleteAccount")
	public ResponseEntity<?> deleteAccount() {
	    return authServices.deleteAccount();
	}

	@GetMapping("/isValid")
	public ResponseEntity<?> tokenValidation() {
	    return authServices.tokenValidation();
	}

	@GetMapping("/me")
	public UserInfoResponse me() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    List<String> roles = auth.getAuthorities()
	        .stream()
	        .map(a -> a.getAuthority())
	        .toList();

	    return new UserInfoResponse(email, roles);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> requestPasswordChangeOtp(
	        @Valid @RequestBody PasswordChangeOtpRequest req) {
	    return authServices.requestPasswordChangeOtp(req);
	}

	@PostMapping("/password-change/verify-otp")
	public ResponseEntity<?> verifyPasswordChangeOtp(
	        @Valid @RequestBody PasswordChangeOtpVerifyRequest req) {
	    return authServices.verifyPasswordChangeOtp(req);
	}

	@PostMapping("/register/request-otp")
	public ResponseEntity<?> requestRegisterOtp(
	        @Valid @RequestBody UserRegister req) {
	    return authServices.requestRegisterOtp(req);
	}

	@PostMapping("/register/verify-otp")
	public ResponseEntity<?> verifyRegisterOtp(
	        @Valid @RequestBody RegisterOtpVerifyRequest req) {
	    return authServices.verifyRegisterOtp(req);
	}
}
