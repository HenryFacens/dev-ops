package com.devops.qas.tests.recommendation.controller;

import com.devops.qas.tests.recommendation.dto.RecommendationDTO;
import com.devops.qas.tests.recommendation.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        given(service.sendRecommendationEmail(1L, "test@test.com")).willReturn(true);

        mockMvc.perform(post("/api/recommendations/1/email")
                        .content("test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}

