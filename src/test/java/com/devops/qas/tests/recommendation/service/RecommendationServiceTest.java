package com.devops.qas.tests.recommendation.service;

import com.devops.qas.tests.recommendation.domain.entity.Recommendation;
import com.devops.qas.tests.recommendation.dto.RecommendationDTO;
import com.devops.qas.tests.recommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository repository;

    @InjectMocks
    private RecommendationService service;

    @Test
    void getRecommendations_ShouldReturnDTOs() {
        Recommendation rec = Recommendation.builder()
                .id(1L)
                .studentId(1L)
                .courseName("Course 1")
                .category("Tech")
                .isSaved(true)
                .isUseful(false)
                .build();
        when(repository.findByStudentId(1L)).thenReturn(List.of(rec));

        List<RecommendationDTO> result = service.getRecommendations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        RecommendationDTO dto = result.get(0);
        assertEquals("Course 1", dto.getCourseName());
        assertEquals("Tech", dto.getCategory());
        assertTrue(dto.isSaved());
        assertFalse(dto.isUseful());
        assertEquals(1L, dto.getStudentId());
        assertEquals(1L, dto.getId());
    }

    @Test
    void sendRecommendationEmail_ShouldReturnTrue_WhenValid() {
        when(repository.findByStudentId(1L)).thenReturn(List.of(new Recommendation()));
        assertTrue(service.sendRecommendationEmail(1L, "test@test.com"));
    }

    @Test
    void sendRecommendationEmail_ShouldReturnFalse_WhenEmailNull() {
        assertFalse(service.sendRecommendationEmail(1L, null));
    }

    @Test
    void sendRecommendationEmail_ShouldReturnFalse_WhenEmailInvalid() {
        assertFalse(service.sendRecommendationEmail(1L, "invalid-email"));
    }

    @Test
    void sendRecommendationEmail_ShouldReturnFalse_WhenNoRecommendations() {
        when(repository.findByStudentId(1L)).thenReturn(Collections.emptyList());
        assertFalse(service.sendRecommendationEmail(1L, "test@test.com"));
    }

    @Test
    void filterRecommendationsByCategory_ShouldReturnFiltered() {
        Recommendation rec = Recommendation.builder().courseName("C1").category("Tech").build();
        when(repository.findByStudentIdAndCategoryContainingIgnoreCase(1L, "Tech")).thenReturn(List.of(rec));

        List<RecommendationDTO> result = service.filterRecommendationsByCategory(1L, "Tech");
        assertEquals(1, result.size());
    }

    @Test
    void filterRecommendationsByCategory_ShouldHandleNullCategory() {
        when(repository.findByStudentIdAndCategoryContainingIgnoreCase(1L, "")).thenReturn(Collections.emptyList());
        service.filterRecommendationsByCategory(1L, null);
        verify(repository).findByStudentIdAndCategoryContainingIgnoreCase(1L, "");
    }

    @Test
    void saveRecommendationForLater_ShouldSaveNew() {
        when(repository.findByStudentId(1L)).thenReturn(Collections.emptyList());
        assertTrue(service.saveRecommendationForLater(1L, "New Course"));
        verify(repository).save(any(Recommendation.class));
    }

    @Test
    void saveRecommendationForLater_ShouldUpdateExisting() {
        Recommendation existing = Recommendation.builder().studentId(1L).courseName("Existing").isSaved(false).build();
        when(repository.findByStudentId(1L)).thenReturn(List.of(existing));
        
        assertTrue(service.saveRecommendationForLater(1L, "Existing"));
        verify(repository).save(argThat(r -> r.isSaved() && r.getCourseName().equals("Existing")));
    }

    @Test
    void saveRecommendationForLater_ShouldReturnFalse_WhenInvalidName() {
        assertFalse(service.saveRecommendationForLater(1L, null));
        assertFalse(service.saveRecommendationForLater(1L, ""));
        assertFalse(service.saveRecommendationForLater(1L, "  "));
    }

    @Test
    void markRecommendationAsUseful_ShouldSaveNew() {
        when(repository.findByStudentId(1L)).thenReturn(Collections.emptyList());
        assertTrue(service.markRecommendationAsUseful(1L, "New Course"));
        verify(repository).save(any(Recommendation.class));
    }

    @Test
    void markRecommendationAsUseful_ShouldUpdateExisting() {
        Recommendation existing = Recommendation.builder().studentId(1L).courseName("Existing").isUseful(false).build();
        when(repository.findByStudentId(1L)).thenReturn(List.of(existing));
        
        assertTrue(service.markRecommendationAsUseful(1L, "Existing"));
        verify(repository).save(argThat(r -> r.isUseful() && r.getCourseName().equals("Existing")));
    }

    @Test
    void markRecommendationAsUseful_ShouldReturnFalse_WhenInvalidName() {
        assertFalse(service.markRecommendationAsUseful(1L, null));
        assertFalse(service.markRecommendationAsUseful(1L, ""));
        assertFalse(service.markRecommendationAsUseful(1L, "  "));
    }
}
