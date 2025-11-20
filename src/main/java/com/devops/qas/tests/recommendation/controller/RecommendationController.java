package com.devops.qas.tests.recommendation.controller;

import com.devops.qas.tests.recommendation.dto.RecommendationDTO;
import com.devops.qas.tests.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping("/{studentId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(@PathVariable Long studentId) {
        return ResponseEntity.ok(service.getRecommendations(studentId));
    }

    @PostMapping("/{studentId}/email")
    public ResponseEntity<Boolean> sendRecommendationEmail(@PathVariable Long studentId, @RequestBody String email) {
        return ResponseEntity.ok(service.sendRecommendationEmail(studentId, email));
    }

    @GetMapping("/{studentId}/filter")
    public ResponseEntity<List<RecommendationDTO>> filterRecommendations(@PathVariable Long studentId, @RequestParam String category) {
        return ResponseEntity.ok(service.filterRecommendationsByCategory(studentId, category));
    }

    @PostMapping("/{studentId}/save")
    public ResponseEntity<Boolean> saveRecommendation(@PathVariable Long studentId, @RequestParam String courseName) {
        return ResponseEntity.ok(service.saveRecommendationForLater(studentId, courseName));
    }

    @PostMapping("/{studentId}/useful")
    public ResponseEntity<Boolean> markAsUseful(@PathVariable Long studentId, @RequestParam String courseName) {
        return ResponseEntity.ok(service.markRecommendationAsUseful(studentId, courseName));
    }
}

