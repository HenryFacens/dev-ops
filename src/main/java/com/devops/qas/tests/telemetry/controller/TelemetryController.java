package com.devops.qas.tests.telemetry.controller;

import com.devops.qas.tests.telemetry.dto.StudySessionEventDTO;
import com.devops.qas.tests.telemetry.service.TelemetryEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryEventService telemetryEventService;

    @GetMapping("/events")
    public ResponseEntity<List<StudySessionEventDTO>> getEvents(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(telemetryEventService.findByFilters(studentId, category));
    }

    @PostMapping("/events")
    public ResponseEntity<StudySessionEventDTO> ingestEvent(@RequestBody StudySessionEventDTO eventDTO) {
        return ResponseEntity.ok(telemetryEventService.saveEvent(eventDTO));
    }
}

