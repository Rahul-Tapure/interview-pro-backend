package com.interviewpro.interviewpro.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.genai.Client;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
    
    @Bean
    public Client geminiClient(@Value("${genKey}") String genKey) {
        return Client.builder().apiKey(genKey).build();
    }
}
