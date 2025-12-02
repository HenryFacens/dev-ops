package com.devops.qas.tests.recommendation.controller;

import com.devops.qas.tests.recommendation.service.CategoryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class CategoryReportController {

    private final CategoryReportService reportService;

    @PostMapping("/category/{category}")
    public ResponseEntity<String> generateCategoryReport(
            @PathVariable String category,
            @RequestParam(defaultValue = "pedrogamerp@gmail.com") String email) {
        reportService.generateAndSendCategoryReport(category, email);
        return ResponseEntity.ok("Relatório de categoria '" + category + "' enviado para processamento de email!");
    }

    @PostMapping("/all-categories")
    public ResponseEntity<String> generateAllCategoriesReport(
            @RequestParam(defaultValue = "pedrogamerp@gmail.com") String email) {
        reportService.generateAndSendAllCategoriesReport(email);
        return ResponseEntity.ok("Relatórios de todas as categorias enviados para processamento de email!");
    }
}

