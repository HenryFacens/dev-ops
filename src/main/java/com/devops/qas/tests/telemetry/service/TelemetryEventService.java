package com.devops.qas.tests.telemetry.service;

import com.devops.qas.tests.telemetry.domain.entity.StudySessionEvent;
import com.devops.qas.tests.telemetry.dto.StudySessionEventDTO;
import com.devops.qas.tests.telemetry.repository.StudySessionEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryEventService {

    private final StudySessionEventRepository repository;
    private final ObjectMapper objectMapper;

    public void processIncomingPayload(String payload) {
        try {
            StudySessionEventDTO dto = objectMapper.readValue(payload, StudySessionEventDTO.class);
            saveEvent(dto);
        } catch (IOException e) {
            log.error("Erro ao processar payload MQTT: {}", payload, e);
        }
    }

    public StudySessionEventDTO saveEvent(StudySessionEventDTO dto) {
        StudySessionEvent entity = toEntity(dto, serializeMetadata(dto.getMetadata()));
        StudySessionEvent saved = repository.save(entity);
        return toDTO(saved);
    }

    public List<StudySessionEventDTO> getRecentEvents() {
        return repository.findTop50ByOrderByReceivedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<StudySessionEventDTO> findByFilters(Long studentId, String category) {
        if (studentId != null) {
            return repository.findByStudentIdOrderByReceivedAtDesc(studentId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        if (StringUtils.hasText(category)) {
            return repository.findByCategoryIgnoreCaseOrderByReceivedAtDesc(category).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        return getRecentEvents();
    }

    private StudySessionEvent toEntity(StudySessionEventDTO dto, String metadataJson) {
        return StudySessionEvent.builder()
                .id(dto.getId())
                .studentId(dto.getStudentId())
                .deviceId(dto.getDeviceId())
                .category(dto.getCategory())
                .courseName(dto.getCourseName())
                .durationMinutes(dto.getDurationMinutes())
                .engagementScore(dto.getEngagementScore())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .metadataJson(metadataJson)
                .receivedAt(dto.getReceivedAt() != null ? dto.getReceivedAt() : OffsetDateTime.now())
                .build();
    }

    private StudySessionEventDTO toDTO(StudySessionEvent entity) {
        return StudySessionEventDTO.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .deviceId(entity.getDeviceId())
                .category(entity.getCategory())
                .courseName(entity.getCourseName())
                .durationMinutes(entity.getDurationMinutes())
                .engagementScore(entity.getEngagementScore())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .metadata(deserializeMetadata(entity.getMetadataJson()))
                .receivedAt(entity.getReceivedAt())
                .build();
    }

    private String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            log.warn("Falha ao serializar metadata, armazenando string vazia", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializeMetadata(String metadataJson) {
        if (!StringUtils.hasText(metadataJson)) {
            return Collections.emptyMap();
        }
        try {
            JsonNode node = objectMapper.readTree(metadataJson);
            return objectMapper.convertValue(node, Map.class);
        } catch (IOException e) {
            log.warn("Falha ao desserializar metadata JSON", e);
            return Collections.emptyMap();
        }
    }
}

