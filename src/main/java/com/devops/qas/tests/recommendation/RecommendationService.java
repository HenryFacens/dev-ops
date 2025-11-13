package com.devops.qas.tests.recommendation;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Camada de domínio/serviço responsável pela regra mínima
 * para fazer os testes passarem (TDD Passo 2).
 *
 * Mapeamento dos Integrantes (mesma ordem dos testes):
 * - Integrante 1 — Pedro Andrade: getRecommendations
 * - Integrante 2 — Kevyn Rocha: sendRecommendationEmail
 * - Integrante 3 — José Antônio: filterRecommendationsByCategory
 * - Integrante 4 — Henry Santurião: saveRecommendationForLater
 * - Integrante 5 — Luis Augusto: markRecommendationAsUseful
 */
public class RecommendationService {

    // Base simples de recomendações por aluno (dados de exemplo)
    private static final Map<Long, List<String>> BASE_RECS = Map.of(
            1L, List.of("Tecnologia - Curso de DevOps", "Tecnologia - Curso de Testes"),
            2L, List.of("Tecnologia - Algoritmos", "Gestão - Produtividade"),
            3L, List.of("Tecnologia - Redes", "Tecnologia - Cloud"),
            4L, List.of("Tecnologia - DevOps"),
            5L, List.of("Tecnologia - Testes", "Tecnologia - QA"));

    private final Map<Long, Set<String>> savedForLater = new HashMap<>();
    private final Map<Long, Set<String>> usefulMarks = new HashMap<>();

    // --- Integrante 1 ---
    // Pedro Andrade
    /** Retorna recomendações básicas para o aluno. */
    public List<String> getRecommendations(Long alunoId) {
        return new ArrayList<>(BASE_RECS.getOrDefault(alunoId, Collections.emptyList()));
    }

    // --- Integrante 2 ---
    // Kevyn Rocha
    /** "Envia" e-mail (validação simples de e-mail + existir recomendações). */
    public boolean sendRecommendationEmail(Long alunoId, String email) {
        if (email == null)
            return false;
        boolean emailValido = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matcher(email).matches();
        boolean temRecs = !getRecommendations(alunoId).isEmpty();
        return emailValido && temRecs;
    }

    // --- Integrante 3 ---
    // José Antônio
    /** Filtra recomendações por categoria (substring). */
    public List<String> filterRecommendationsByCategory(Long alunoId, String categoria) {
        String cat = categoria == null ? "" : categoria;
        List<String> base = getRecommendations(alunoId);
        List<String> out = new ArrayList<>();
        for (String c : base) {
            if (c.contains(cat))
                out.add(c);
        }
        return out;
    }

    // --- Integrante 4 ---
    // Henry Santurião
    /** Salva um curso na lista "ver depois". */
    public boolean saveRecommendationForLater(Long alunoId, String curso) {
        if (curso == null || curso.isBlank())
            return false;
        savedForLater.computeIfAbsent(alunoId, k -> new HashSet<>()).add(curso);
        return true;
    }

    // --- Integrante 5 ---
    // Luis Augusto
    /** Marca uma recomendação como útil. */
    public boolean markRecommendationAsUseful(Long alunoId, String curso) {
        if (curso == null || curso.isBlank())
            return false;
        usefulMarks.computeIfAbsent(alunoId, k -> new HashSet<>()).add(curso);
        return true;
    }
}
