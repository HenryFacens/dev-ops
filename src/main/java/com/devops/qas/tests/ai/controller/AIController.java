package com.devops.qas.tests.ai.controller;

import com.devops.qas.tests.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/recommendation")
    public ResponseEntity<String> generateRecommendation(@RequestBody String studentProfile) {
        String recommendation = aiService.generateRecommendation(studentProfile);
        return ResponseEntity.ok(recommendation);
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        String response = aiService.chat(message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze-feedback")
    public ResponseEntity<String> analyzeFeedback(@RequestBody String feedback) {
        String analysis = aiService.analyzeRecommendationFeedback(feedback);
        return ResponseEntity.ok(analysis);
    }
}

