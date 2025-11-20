package com.devops.qas.tests.recommendation.controller;

import com.devops.qas.tests.recommendation.dto.RecommendationDTO;
import com.devops.qas.tests.recommendation.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService service;

    @Test
    void getRecommendations_ShouldReturnList() throws Exception {
        RecommendationDTO dto = RecommendationDTO.builder()
                .studentId(1L)
                .courseName("Course 1")
                .build();
        given(service.getRecommendations(1L)).willReturn(List.of(dto));

        mockMvc.perform(get("/api/recommendations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Course 1"));
    }

    @Test
    void sendRecommendationEmail_ShouldReturnTrue() throws Exception {
        given(service.sendRecommendationEmail(anyLong(), anyString())).willReturn(true);

        mockMvc.perform(post("/api/recommendations/1/email")
                        .content("test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void filterRecommendations_ShouldReturnFilteredList() throws Exception {
        RecommendationDTO dto = RecommendationDTO.builder()
                .studentId(1L)
                .category("Tech")
                .build();
        given(service.filterRecommendationsByCategory(1L, "Tech")).willReturn(List.of(dto));

        mockMvc.perform(get("/api/recommendations/1/filter")
                        .param("category", "Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Tech"));
    }

    @Test
    void saveRecommendation_ShouldReturnTrue() throws Exception {
        given(service.saveRecommendationForLater(anyLong(), anyString())).willReturn(true);

        mockMvc.perform(post("/api/recommendations/1/save")
                        .param("courseName", "Course 1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void markAsUseful_ShouldReturnTrue() throws Exception {
        given(service.markRecommendationAsUseful(anyLong(), anyString())).willReturn(true);

        mockMvc.perform(post("/api/recommendations/1/useful")
                        .param("courseName", "Course 1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
