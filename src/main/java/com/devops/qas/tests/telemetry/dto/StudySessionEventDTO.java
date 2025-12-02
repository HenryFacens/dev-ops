package com.devops.qas.tests.telemetry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionEventDTO {
    private Long id;
    private Long studentId;
    private String deviceId;
    private String category;
    private String courseName;
    private Integer durationMinutes;
    private Double engagementScore;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Map<String, Object> metadata;
    private OffsetDateTime receivedAt;
}

