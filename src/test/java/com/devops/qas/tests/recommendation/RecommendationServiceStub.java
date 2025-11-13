package com.devops.qas.tests.recommendation;

import java.util.List;

public class RecommendationServiceStub {

    private final RecommendationService service = new RecommendationService();

    public List<String> getRecommendations(Long alunoId) {
        // Stub: retorna vazio, causando falha no teste
        return service.getRecommendations(alunoId);
    }

    public boolean sendRecommendationEmail(Long alunoId, String email) {
        // Stub: retorna sempre false → falha no teste
        return service.sendRecommendationEmail(alunoId, email);
    }

    public List<String> filterRecommendationsByCategory(Long alunoId, String categoria) {
        // Stub: retorna vazio → falha no teste
        return service.filterRecommendationsByCategory(alunoId, categoria);
    }

    public boolean saveRecommendationForLater(Long alunoId, String curso) {
        // Stub: retorna false → falha no teste
        return service.saveRecommendationForLater(alunoId, curso);
    }

    public boolean markRecommendationAsUseful(Long alunoId, String curso) {
        // Stub: retorna false → falha no teste
        return service.markRecommendationAsUseful(alunoId, curso);
    }
}
