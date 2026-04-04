package com.interviewpro.interviewpro.communication.service;

import org.springframework.stereotype.Service;
import com.interviewpro.interviewpro.ngrok.service.NgrokService;

@Service
public class WebhookUrlService {

    private final NgrokService ngrokService;

    public WebhookUrlService(NgrokService ngrokService) {
        this.ngrokService = ngrokService;
    }

    public String getAssemblyWebhookUrl() {

        String baseUrl = ngrokService.getNgrokUrl();

        if (baseUrl == null) {
            throw new RuntimeException("Ngrok is not running");
        }

        return baseUrl + "/interviewpro/communication/webhook/assemblyai";
    }
}