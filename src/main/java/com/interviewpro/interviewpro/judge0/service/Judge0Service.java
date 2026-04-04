package com.interviewpro.interviewpro.judge0.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.interviewpro.interviewpro.judge0.dto.Judge0Request;
import com.interviewpro.interviewpro.judge0.dto.Judge0Response;
import com.interviewpro.interviewpro.judge0.dto.LanguagesResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Judge0Service {

    private final WebClient webClient;
    

    private static final String JUDGE0_URL =
            "http://13.232.59.226:2358/submissions?base64_encoded=false&wait=true";
    private static final String  LANG_JUDGE0_URL = "http://13.232.59.226:2358/languages";
    public Judge0Response execute(String code, Integer languageId, String input) {

        Judge0Request req = new Judge0Request();
        req.setSourceCode(code);
        req.setLanguageId(languageId);
        req.setStdin(input);

        return webClient.post()
                .uri(JUDGE0_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Judge0Response.class)
                .block(); // synchronous for now
    }
    
    public LanguagesResponse[] getLanguages() {
        return webClient.get()
            .uri(LANG_JUDGE0_URL)
            .retrieve()
            .bodyToMono(LanguagesResponse[].class) // Judge0 returns array
            .block(); // Blocking call - consider using reactive approach in production
    }
}
