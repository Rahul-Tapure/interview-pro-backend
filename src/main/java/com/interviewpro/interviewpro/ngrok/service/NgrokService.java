package com.interviewpro.interviewpro.ngrok.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NgrokService {

    public String getNgrokUrl() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map response = restTemplate.getForObject(
                    "http://localhost:4040/api/tunnels",
                    Map.class
            );

            var tunnels = (java.util.List<Map>) response.get("tunnels");

            for (Map tunnel : tunnels) {
                String publicUrl = (String) tunnel.get("public_url");

                if (publicUrl.startsWith("https")) {
                    return publicUrl;
                }
            }

        } catch (Exception e) {
            System.out.println("Ngrok not running: " + e.getMessage());
        }

        return null;
    }

}