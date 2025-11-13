package com.devops.qas.tests.recommendation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class RecommendationTests {

    // --- Integrante 1 ---
    // Pedro Andrade
    @Test
    void deveExibirRecomendacoesNaPaginaInicialQuandoAlunoConcluiuCurso() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        Long alunoId = 1L;
        List<String> recomendacoes = service.getRecommendations(alunoId);
        assertNotNull(recomendacoes);
        assertFalse(recomendacoes.isEmpty());
    }

    // --- Integrante 2 ---
    // Kevyn Rocha
    @Test
    void deveEnviarEmailComRecomendacoesQuandoAlunoPossuiEmailValido() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        Long alunoId = 2L;
        boolean enviado = service.sendRecommendationEmail(alunoId, "aluno@teste.com");
        assertTrue(enviado);
    }

    // --- Integrante 3 ---
    // José Antônio
    @Test
    void deveFiltrarRecomendacoesPorCategoriaQuandoAlunoSelecionaCategoria() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        Long alunoId = 3L;
        List<String> recomendacoes = service.filterRecommendationsByCategory(alunoId, "Tecnologia");
        assertNotNull(recomendacoes);
        assertFalse(recomendacoes.isEmpty());
        assertTrue(recomendacoes.stream().allMatch(curso -> curso.contains("Tecnologia")));
    }

    // --- Integrante 4 ---
    // Henry Santurião
    @Test
    void deveSalvarCursoNaListaPessoalQuandoAlunoClicaEmSalvarParaDepois() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        Long alunoId = 4L;
        String curso = "Curso de DevOps";
        boolean salvo = service.saveRecommendationForLater(alunoId, curso);
        assertTrue(salvo);
    }

    // --- Integrante 5 ---
    // Luis Augusto
    @Test
    void deveRegistrarAvaliacaoQuandoAlunoMarcaRecomendacaoComoUtil() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        Long alunoId = 5L;
        String curso = "Curso de Testes";
        boolean registrado = service.markRecommendationAsUseful(alunoId, curso);
        assertTrue(registrado);
    }

    // ============================
    // NOVOS TESTES PARA COBRIR RAMOS
    // ============================

    // --- Integrante 2 (Kevyn Rocha) ---
    @Test
    void naoDeveEnviarEmailQuandoEmailForNull() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        assertFalse(service.sendRecommendationEmail(2L, null));
    }

    @Test
    void naoDeveEnviarEmailQuandoEmailForInvalido() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        assertFalse(service.sendRecommendationEmail(2L, "aluno@"));
    }

    @Test
    void naoDeveEnviarEmailQuandoNaoHouverRecomendacoes() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        assertFalse(service.sendRecommendationEmail(999L, "ok@teste.com"));
    }

    // --- Integrante 3 (José Antônio) ---
    @Test
    void deveRetornarListaCompletaQuandoCategoriaForNull() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        List<String> lista = service.filterRecommendationsByCategory(3L, null);
        assertFalse(lista.isEmpty());
    }

    @Test
    void deveRetornarListaVaziaQuandoCategoriaNaoExistir() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        List<String> lista = service.filterRecommendationsByCategory(3L, "Medicina");
        assertTrue(lista.isEmpty());
    }

    // --- Integrante 4 (Henry Santurião) ---
    @Test
    void naoDeveSalvarCursoQuandoForNullOuBlank() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        assertFalse(service.saveRecommendationForLater(4L, null));
        assertFalse(service.saveRecommendationForLater(4L, "   "));
    }

    // --- Integrante 5 (Luis Augusto) ---
    @Test
    void naoDeveRegistrarAvaliacaoQuandoCursoForNullOuBlank() {
        RecommendationServiceStub service = new RecommendationServiceStub();
        assertFalse(service.markRecommendationAsUseful(5L, null));
        assertFalse(service.markRecommendationAsUseful(5L, ""));
    }
}
