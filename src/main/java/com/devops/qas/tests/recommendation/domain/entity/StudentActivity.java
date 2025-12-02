package com.devops.qas.tests.recommendation.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "location", nullable = false)
    private String location; // Ex: "Library", "DevOps Lab", "Cafeteria"

    @Column(name = "action", nullable = false)
    private String action; // Ex: "CHECK_IN", "CHECK_OUT", "BOOK_LOAN"

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "device_id")
    private String deviceId; // ID do sensor/totem
}

