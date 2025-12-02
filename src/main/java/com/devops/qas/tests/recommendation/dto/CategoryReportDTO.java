package com.devops.qas.tests.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryReportDTO {
    private String category;
    private Long totalRecommendations;
    private Long totalStudents;
    private Long savedCount;
    private Long usefulCount;
    private List<RecommendationSummary> topRecommendations;
    private LocalDateTime reportDate;
    private String recipientEmail;
    private boolean hasRecommendations;
    private String noticeMessage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationSummary {
        private String courseName;
        private Long count;
        private Double usefulPercentage;
    }
}

