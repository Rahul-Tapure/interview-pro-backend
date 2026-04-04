package com.interviewpro.interviewpro.contact.controller;


import com.interviewpro.interviewpro.contact.dto.ContactRequest;
import com.interviewpro.interviewpro.contact.service.BrevoEmailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/interviewpro/api/contact")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final BrevoEmailService emailService;

    public ContactController(BrevoEmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> submitContactForm(
            @Valid @RequestBody ContactRequest request) {

        log.info("Contact form received from: {}", request.getEmail());

        try {
            // Send confirmation email to the user
            emailService.sendConfirmationEmail(request);

            // Notify admin/support team
            emailService.sendAdminNotification(request);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Thank you for contacting us! We'll get back to you within 24 hours."
            ));

        } catch (Exception e) {
            log.error("Failed to process contact form submission", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Something went wrong. Please try again later."
            ));
        }
    }
}
