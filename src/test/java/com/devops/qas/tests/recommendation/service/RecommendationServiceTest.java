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
import static org.mockito.ArgumentMatchers.anyLong;
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
                .build();
        when(repository.findByStudentId(1L)).thenReturn(List.of(rec));

        List<RecommendationDTO> result = service.getRecommendations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Course 1", result.get(0).getCourseName());
    }

    @Test
    void sendRecommendationEmail_ShouldReturnTrue_WhenValid() {
        when(repository.findByStudentId(1L)).thenReturn(List.of(new Recommendation()));
        
        boolean result = service.sendRecommendationEmail(1L, "test@test.com");
        
        assertTrue(result);
    }

    @Test
    void saveRecommendationForLater_ShouldSaveNew() {
        when(repository.findByStudentId(1L)).thenReturn(Collections.emptyList());
        
        boolean result = service.saveRecommendationForLater(1L, "New Course");
        
        assertTrue(result);
        verify(repository).save(any(Recommendation.class));
    }

    @Test
    void saveRecommendationForLater_ShouldUpdateExisting() {
        Recommendation existing = Recommendation.builder()
                .studentId(1L)
                .courseName("Existing")
                .isSaved(false)
                .build();
        when(repository.findByStudentId(1L)).thenReturn(List.of(existing));
        
        boolean result = service.saveRecommendationForLater(1L, "Existing");
        
        assertTrue(result);
        verify(repository).save(argThat(r -> r.isSaved() && r.getCourseName().equals("Existing")));
    }
}
