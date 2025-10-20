package com.fitness.aiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // The constructor now correctly takes the WebClient bean provided by your config.
    public GeminiService(WebClient webClient) {
        this.webClient = webClient;
    }

    // This is the corrected method.
    public String getAnswer(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        try {
            return webClient.post()
                    // ✅ FIX: Correctly constructs the URL with "?key="
                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Using .block() for simplicity in a non-reactive context

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            return "{\"error\":\"API call failed: " + e.getMessage() + "\"}";
        }
    }
}