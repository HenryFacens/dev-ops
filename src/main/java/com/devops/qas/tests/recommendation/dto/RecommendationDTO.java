package com.devops.qas.tests.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Long id;
    private Long studentId;
    private String courseName;
    private String category;
    private boolean isSaved;
    private boolean isUseful;
}

