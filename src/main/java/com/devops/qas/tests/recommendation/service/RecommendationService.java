package com.devops.qas.tests.recommendation.service;

import com.devops.qas.tests.recommendation.domain.entity.Recommendation;
import com.devops.qas.tests.recommendation.dto.RecommendationDTO;
import com.devops.qas.tests.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;

    public List<RecommendationDTO> getRecommendations(Long studentId) {
        return repository.findByStudentId(studentId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public boolean sendRecommendationEmail(Long studentId, String email) {
        if (email == null)
            return false;
        boolean emailValido = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matcher(email).matches();
        boolean temRecs = !repository.findByStudentId(studentId).isEmpty();
        return emailValido && temRecs;
    }

    public List<RecommendationDTO> filterRecommendationsByCategory(Long studentId, String category) {
        String cat = category == null ? "" : category;
        return repository.findByStudentIdAndCategoryContainingIgnoreCase(studentId, cat).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public boolean saveRecommendationForLater(Long studentId, String courseName) {
        if (courseName == null || courseName.isBlank())
            return false;
        
        // Find if exists or create new
        Optional<Recommendation> existing = repository.findByStudentId(studentId).stream()
                .filter(r -> r.getCourseName().equals(courseName))
                .findFirst();

        Recommendation rec;
        if (existing.isPresent()) {
            rec = existing.get();
            rec.setSaved(true);
        } else {
            rec = Recommendation.builder()
                    .studentId(studentId)
                    .courseName(courseName)
                    .isSaved(true)
                    .build();
        }
        repository.save(rec);
        return true;
    }

    public boolean markRecommendationAsUseful(Long studentId, String courseName) {
        if (courseName == null || courseName.isBlank())
            return false;

        Optional<Recommendation> existing = repository.findByStudentId(studentId).stream()
                .filter(r -> r.getCourseName().equals(courseName))
                .findFirst();

        Recommendation rec;
        if (existing.isPresent()) {
            rec = existing.get();
            rec.setUseful(true);
        } else {
            rec = Recommendation.builder()
                    .studentId(studentId)
                    .courseName(courseName)
                    .isUseful(true)
                    .build();
        }
        repository.save(rec);
        return true;
    }

    private RecommendationDTO toDTO(Recommendation entity) {
        return RecommendationDTO.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .courseName(entity.getCourseName())
                .category(entity.getCategory())
                .isSaved(entity.isSaved())
                .isUseful(entity.isUseful())
                .build();
    }
}
