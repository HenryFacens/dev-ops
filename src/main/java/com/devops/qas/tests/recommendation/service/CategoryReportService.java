package com.devops.qas.tests.recommendation.service;

import com.devops.qas.tests.messaging.config.RabbitMQConfig;
import com.devops.qas.tests.messaging.service.RabbitMQService;
import com.devops.qas.tests.recommendation.domain.entity.Recommendation;
import com.devops.qas.tests.recommendation.dto.CategoryReportDTO;
import com.devops.qas.tests.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryReportService {

    private final RecommendationRepository repository;
    private final RabbitMQService rabbitMQService;

    public void generateAndSendCategoryReport(String category, String recipientEmail) {
        log.info("Gerando relatório de categoria: {} para email: {}", category, recipientEmail);
        
        List<Recommendation> recommendations = repository.findByCategoryContainingIgnoreCase(category);
        boolean hasData = !recommendations.isEmpty();

        if (!hasData) {
            log.warn("Nenhuma recomendação encontrada para a categoria: {}", category);
        }

        CategoryReportDTO report = buildCategoryReport(
                category,
                recommendations,
                recipientEmail,
                hasData,
                hasData
                        ? null
                        : String.format("Nenhuma recomendação encontrada para a categoria '%s' no momento.", category)
        );
        
        log.info("Enviando relatório para RabbitMQ: {}", report);
        rabbitMQService.sendMessage(
                RabbitMQConfig.EMAIL_REPORTS_EXCHANGE,
                RabbitMQConfig.EMAIL_REPORTS_ROUTING_KEY,
                report
        );
        
        log.info("Relatório enviado com sucesso para processamento de email!");
    }

    public void generateAndSendAllCategoriesReport(String recipientEmail) {
        log.info("Gerando relatório de todas as categorias para email: {}", recipientEmail);
        
        List<Recommendation> allRecommendations = repository.findAll();
        
        if (allRecommendations.isEmpty()) {
            log.warn("Nenhuma recomendação encontrada no sistema");
            CategoryReportDTO emptyReport = buildCategoryReport(
                    "Sem Dados",
                    List.of(),
                    recipientEmail,
                    false,
                    "Nenhuma recomendação cadastrada no sistema no momento."
            );
            rabbitMQService.sendMessage(
                    RabbitMQConfig.EMAIL_REPORTS_EXCHANGE,
                    RabbitMQConfig.EMAIL_REPORTS_ROUTING_KEY,
                    emptyReport
            );
            return;
        }

        Map<String, List<Recommendation>> byCategory = groupByCategory(allRecommendations);

        byCategory.forEach((category, recommendations) -> {
            CategoryReportDTO report = buildCategoryReport(
                    category,
                    recommendations,
                    recipientEmail,
                    true,
                    null
            );
            rabbitMQService.sendMessage(
                    RabbitMQConfig.EMAIL_REPORTS_EXCHANGE,
                    RabbitMQConfig.EMAIL_REPORTS_ROUTING_KEY,
                    report
            );
        });

        log.info("Relatórios de todas as categorias enviados com sucesso!");
    }

    private Map<String, List<Recommendation>> groupByCategory(List<Recommendation> recommendations) {
        return recommendations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCategory() != null && !r.getCategory().isEmpty()
                                ? r.getCategory()
                                : "Sem Categoria"
                ));
    }

    private CategoryReportDTO buildCategoryReport(
            String category,
            List<Recommendation> recommendations,
            String recipientEmail,
            boolean hasData,
            String noticeMessage
    ) {
        long totalRecommendations = recommendations.size();
        long totalStudents = recommendations.stream()
                .map(Recommendation::getStudentId)
                .distinct()
                .count();
        long savedCount = recommendations.stream()
                .filter(Recommendation::isSaved)
                .count();
        long usefulCount = recommendations.stream()
                .filter(Recommendation::isUseful)
                .count();

        // Top recomendações por curso
        Map<String, List<Recommendation>> byCourse = recommendations.stream()
                .collect(Collectors.groupingBy(Recommendation::getCourseName));

        List<CategoryReportDTO.RecommendationSummary> topRecommendations = byCourse.entrySet().stream()
                .map(entry -> {
                    List<Recommendation> courseRecs = entry.getValue();
                    long courseUsefulCount = courseRecs.stream()
                            .filter(Recommendation::isUseful)
                            .count();
                    double usefulPercentage = courseRecs.size() > 0 
                            ? (double) courseUsefulCount / courseRecs.size() * 100 
                            : 0.0;

                    return CategoryReportDTO.RecommendationSummary.builder()
                            .courseName(entry.getKey())
                            .count((long) courseRecs.size())
                            .usefulPercentage(Math.round(usefulPercentage * 100.0) / 100.0)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(10)
                .collect(Collectors.toList());

        return CategoryReportDTO.builder()
                .category(category)
                .totalRecommendations(totalRecommendations)
                .totalStudents(totalStudents)
                .savedCount(savedCount)
                .usefulCount(usefulCount)
                .topRecommendations(topRecommendations)
                .reportDate(LocalDateTime.now())
                .recipientEmail(recipientEmail)
                .hasRecommendations(hasData)
                .noticeMessage(noticeMessage)
                .build();
    }
}

