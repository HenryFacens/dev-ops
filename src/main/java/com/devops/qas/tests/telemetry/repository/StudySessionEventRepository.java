package com.devops.qas.tests.telemetry.repository;

import com.devops.qas.tests.telemetry.domain.entity.StudySessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudySessionEventRepository extends JpaRepository<StudySessionEvent, Long> {

    List<StudySessionEvent> findTop50ByOrderByReceivedAtDesc();

    List<StudySessionEvent> findByStudentIdOrderByReceivedAtDesc(Long studentId);

    List<StudySessionEvent> findByCategoryIgnoreCaseOrderByReceivedAtDesc(String category);
}

