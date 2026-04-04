package com.interviewpro.interviewpro.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.interviewpro.interviewpro.auth.entity.UserLogin;
import com.interviewpro.interviewpro.auth.entity.UserRegister;
import com.interviewpro.interviewpro.auth.entity.UserTable;
import com.interviewpro.interviewpro.auth.jwt.JwtService;
import com.interviewpro.interviewpro.auth.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


import com.interviewpro.interviewpro.auth.dto.EmailChangeOtpRequest;
import com.interviewpro.interviewpro.auth.dto.EmailChangeOtpVerifyRequest;
import com.interviewpro.interviewpro.auth.dto.PasswordChangeOtpRequest;
import com.interviewpro.interviewpro.auth.dto.PasswordChangeOtpVerifyRequest;
import com.interviewpro.interviewpro.auth.dto.RegisterOtpVerifyRequest;
import com.interviewpro.interviewpro.auth.entity.AuthOtpCode;

import com.interviewpro.interviewpro.auth.repository.AuthOtpCodeRepository;

import com.interviewpro.interviewpro.contact.service.BrevoEmailService;


@Service
public class AuthServices {
	@Autowired
	private UserRepository usersTableRepo;

	@Autowired
	private AuthOtpCodeRepository authOtpCodeRepository;

	@Autowired
	private BrevoEmailService brevoMailService;
	

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
 

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int OTP_EXPIRY_MINUTES = 10;
	private static final String PURPOSE_PASSWORD_CHANGE = "PASSWORD_CHANGE";
	   private static final String PURPOSE_REGISTER = "REGISTER";

    public ResponseEntity<?> login(@Valid UserLogin req) {

        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        String token = jwtService.generateToken(req.getEmail());

        ResponseCookie cookie = ResponseCookie.from("entrypasstoken", token)
                .httpOnly(true)
                .secure(false)        // true in production (HTTPS)
                .sameSite("Lax")   // use "None" if frontend is on different domain
                .path("/")
                .maxAge(20 * 24 * 60 * 60)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("message", "Login successful"));
    }

    /* ================= REGISTER ================= */

    @Transactional
    public ResponseEntity<?> register(@Valid UserRegister reg) {

        String email = reg.getEmail().trim().toLowerCase();

        if (usersTableRepo.existsById(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "User already exists"));
        }

        UserTable user = UserTable.builder()
                .email(email)
                .password(passwordEncoder.encode(reg.getPassword()))
                .role(reg.getRole())
                .build();

        usersTableRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Successfully created"));
    }

    /* ================= LOGOUT ================= */

    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie.from("entrypasstoken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("message", "Successfully logged out"));
    }


    /* ================= DELETE ACCOUNT ================= */

    @Transactional
    public ResponseEntity<?> deleteAccount() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersTableRepo.deleteById(email);

        ResponseCookie cookie = ResponseCookie.from("entrypasstoken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("message", "Account deleted successfully"));
}

    /* ================= TOKEN VALIDATION ================= */

    public ResponseEntity<?> tokenValidation() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (!usersTableRepo.existsById(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(Map.of(
                "email", email,
                "valid", true
        ));
    }

    public ResponseEntity<?> requestPasswordChangeOtp(PasswordChangeOtpRequest req) {

        String requestedEmail = req.getEmail().trim().toLowerCase();

        Optional<UserTable> user = usersTableRepo.findByEmail(requestedEmail);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        String otp = generateOtp();
        saveOtp(requestedEmail, PURPOSE_PASSWORD_CHANGE, otp, null);

        brevoMailService.sendOtpMail(
                requestedEmail,
                "Password Change OTP",
                "Your OTP for password change is: " + otp + ". It expires in 10 minutes."
        );

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

	@Transactional
	public ResponseEntity<?> verifyPasswordChangeOtp(PasswordChangeOtpVerifyRequest req) {
	   
	    String email = req.getEmail().trim().toLowerCase();

	    if (!email.equalsIgnoreCase(email)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	            .body(Map.of("message", "You can update only your own password"));
	    }

	    AuthOtpCode otpRow = authOtpCodeRepository
	        .findTopByOwnerEmailAndPurposeAndConsumedFalseOrderByCreatedAtDesc(email, PURPOSE_PASSWORD_CHANGE)
	        .orElse(null);

	    if (otpRow == null || otpRow.getExpiresAt().isBefore(LocalDateTime.now())) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(Map.of("message", "OTP expired or not found"));
	    }

	    if (!passwordEncoder.matches(req.getOtp(), otpRow.getOtpHash())) {
	        otpRow.setAttempts(otpRow.getAttempts() + 1);
	        authOtpCodeRepository.save(otpRow);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(Map.of("message", "Invalid OTP"));
	    }

	    UserTable user = usersTableRepo.findById(email).orElse(null);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(Map.of("message", "User not found"));
	    }

	    user.setPassword(passwordEncoder.encode(req.getNewPassword()));
	    usersTableRepo.save(user);

	    otpRow.setConsumed(true);
	    authOtpCodeRepository.save(otpRow);

	    return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
	}


	private void saveOtp(String ownerEmail, String purpose, String otp, String targetValue) {
	    AuthOtpCode row = new AuthOtpCode();
	    row.setOwnerEmail(ownerEmail);
	    row.setPurpose(purpose);
	    row.setOtpHash(passwordEncoder.encode(otp));
	    row.setTargetValue(targetValue);
	    row.setAttempts(0);
	    row.setConsumed(false);
	    row.setCreatedAt(LocalDateTime.now());
	    row.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
	    authOtpCodeRepository.save(row);
	}

	private String generateOtp() {
	    int code = 100000 + RANDOM.nextInt(900000);
	    return String.valueOf(code);
	}
	
	public ResponseEntity<?> requestRegisterOtp(UserRegister req) {

	    String email = req.getEmail().trim().toLowerCase();

	    if (usersTableRepo.existsById(email)) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of("message", "User already exists"));
	    }

	    String otp = generateOtp();

	    saveOtp(email, PURPOSE_REGISTER, otp, req.getPassword() + "|" + req.getRole());

	    brevoMailService.sendOtpMail(
	            email,
	            "InterviewPro Registration OTP",
	            "Your OTP for registration is: " + otp + ". It expires in 10 minutes."
	    );

	    return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
	}
	
	@Transactional
	public ResponseEntity<?> verifyRegisterOtp(RegisterOtpVerifyRequest req) {

	    String email = req.getEmail().trim().toLowerCase();

	    AuthOtpCode otpRow = authOtpCodeRepository
	            .findTopByOwnerEmailAndPurposeAndConsumedFalseOrderByCreatedAtDesc(email, PURPOSE_REGISTER)
	            .orElse(null);

	    if (otpRow == null || otpRow.getExpiresAt().isBefore(LocalDateTime.now())) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("message", "OTP expired or not found"));
	    }

	    if (!passwordEncoder.matches(req.getOtp(), otpRow.getOtpHash())) {

	        otpRow.setAttempts(otpRow.getAttempts() + 1);
	        authOtpCodeRepository.save(otpRow);

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("message", "Invalid OTP"));
	    }

	    String[] data = otpRow.getTargetValue().split("\\|");

	    String password = data[0];
	    String role = data[1];

	    UserTable user = UserTable.builder()
	            .email(email)
	            .password(passwordEncoder.encode(password))
	            .role(role)
	            .build();

	    usersTableRepo.save(user);

	    otpRow.setConsumed(true);
	    authOtpCodeRepository.save(otpRow);

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(Map.of("message", "Account created successfully"));
	}
	}
