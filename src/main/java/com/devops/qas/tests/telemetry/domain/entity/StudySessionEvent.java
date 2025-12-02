package com.devops.qas.tests.telemetry.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "study_session_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "category")
    private String category;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "engagement_score")
    private Double engagementScore;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "received_at")
    private OffsetDateTime receivedAt;

    @PrePersist
    public void onPersist() {
        if (receivedAt == null) {
            receivedAt = OffsetDateTime.now();
        }
    }
}

