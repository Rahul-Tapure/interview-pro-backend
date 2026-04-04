package com.interviewpro.interviewpro.contact.service;


import com.interviewpro.interviewpro.contact.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate;

    public BrevoEmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a confirmation email to the user who submitted the contact form,
     * letting them know their message was received and they'll be contacted later.
     */
    public void sendConfirmationEmail(ContactRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = Map.of(
            "sender", Map.of("name", senderName, "email", senderEmail),
            "to", List.of(Map.of("email", request.getEmail(), "name", request.getName())),
            "subject", "We received your message — " + request.getSubject(),
            "htmlContent", buildConfirmationHtml(request)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send confirmation email. Brevo responded with: "
                    + response.getStatusCode());
        }
    }

    /**
     * Sends a notification email to the admin/support team about a new contact form submission.
     */
    public void sendAdminNotification(ContactRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = Map.of(
            "sender", Map.of("name", senderName, "email", senderEmail),
            "to", List.of(Map.of("email", senderEmail, "name", senderName)),
            "subject", "[Contact Form] " + request.getCategory() + " — " + request.getSubject(),
            "htmlContent", buildAdminNotificationHtml(request)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.exchange(BREVO_API_URL, HttpMethod.POST, entity, String.class);
    }

    private String buildConfirmationHtml(ContactRequest request) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; background: #f4f4f7; margin: 0; padding: 0; }
                .container { max-width: 560px; margin: 40px auto; background: #ffffff; border-radius: 12px;
                             overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,.08); }
                .header { background: linear-gradient(135deg, #6366f1, #818cf8); padding: 32px; text-align: center; }
                .header h1 { color: #fff; margin: 0; font-size: 22px; }
                .body { padding: 32px; color: #334155; line-height: 1.7; }
                .body h2 { color: #1e293b; font-size: 18px; margin-top: 0; }
                .highlight { background: #f1f5f9; border-left: 4px solid #6366f1; padding: 12px 16px;
                             border-radius: 6px; margin: 16px 0; font-size: 14px; color: #475569; }
                .footer { padding: 20px 32px; text-align: center; font-size: 12px; color: #94a3b8; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>InterviewPro</h1>
                </div>
                <div class="body">
                  <h2>Hi %s,</h2>
                  <p>Thank you for reaching out to us! We've received your message and our team will
                     review it shortly.</p>
                  <div class="highlight">
                    <strong>Subject:</strong> %s<br/>
                    <strong>Category:</strong> %s
                  </div>
                  <p>We'll get back to you within <strong>24 hours</strong>. In the meantime,
                     feel free to explore more on our platform.</p>
                  <p>Best regards,<br/><strong>Team InterviewPro</strong></p>
                </div>
                <div class="footer">
                  &copy; 2026 InterviewPro. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(request.getName(), request.getSubject(), request.getCategory());
    }

    private String buildAdminNotificationHtml(ContactRequest request) {
        return """
            <h2>New Contact Form Submission</h2>
            <p><strong>Name:</strong> %s</p>
            <p><strong>Email:</strong> %s</p>
            <p><strong>Subject:</strong> %s</p>
            <p><strong>Category:</strong> %s</p>
            <p><strong>Message:</strong></p>
            <blockquote>%s</blockquote>
            """.formatted(
                request.getName(),
                request.getEmail(),
                request.getSubject(),
                request.getCategory(),
                request.getMessage()
            );
    }
    
    public void sendOtpMail(String toEmail, String subject, String bodyText) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> payload = Map.of(
            "sender", Map.of("name", senderName, "email", senderEmail),
            "to", new Object[] { Map.of("email", toEmail) },
            "subject", subject,
            "htmlContent", "<p>" + bodyText + "</p>"
        );

        restTemplate.postForEntity(BREVO_API_URL, new HttpEntity<>(payload, headers), String.class);
    }
}
