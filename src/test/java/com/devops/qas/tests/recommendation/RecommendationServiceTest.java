package com.devops.qas.tests.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do RecommendationService")
class RecommendationServiceTest {

    private RecommendationService service;

    @BeforeEach
    void setup() {
        service = new RecommendationService();
    }

    // ========== Testes do Integrante 1: Pedro Andrade ==========

    @Test
    @DisplayName("Deve retornar recomendações para aluno existente (ID 1)")
    void deveRetornarRecomendacoesParaAluno1() {
        // Act
        List<String> recomendacoes = service.getRecommendations(1L);

        // Assert
        assertNotNull(recomendacoes);
        assertEquals(2, recomendacoes.size());
        assertTrue(recomendacoes.contains("Tecnologia - Curso de DevOps"));
        assertTrue(recomendacoes.contains("Tecnologia - Curso de Testes"));
    }

    @Test
    @DisplayName("Deve retornar recomendações para aluno existente (ID 2)")
    void deveRetornarRecomendacoesParaAluno2() {
        // Act
        List<String> recomendacoes = service.getRecommendations(2L);

        // Assert
        assertEquals(2, recomendacoes.size());
        assertTrue(recomendacoes.contains("Tecnologia - Algoritmos"));
        assertTrue(recomendacoes.contains("Gestão - Produtividade"));
    }

    @Test
    @DisplayName("Deve retornar recomendações para aluno existente (ID 3)")
    void deveRetornarRecomendacoesParaAluno3() {
        // Act
        List<String> recomendacoes = service.getRecommendations(3L);

        // Assert
        assertEquals(2, recomendacoes.size());
        assertTrue(recomendacoes.contains("Tecnologia - Redes"));
        assertTrue(recomendacoes.contains("Tecnologia - Cloud"));
    }

    @Test
    @DisplayName("Deve retornar recomendações para aluno existente (ID 4)")
    void deveRetornarRecomendacoesParaAluno4() {
        // Act
        List<String> recomendacoes = service.getRecommendations(4L);

        // Assert
        assertEquals(1, recomendacoes.size());
        assertTrue(recomendacoes.contains("Tecnologia - DevOps"));
    }

    @Test
    @DisplayName("Deve retornar recomendações para aluno existente (ID 5)")
    void deveRetornarRecomendacoesParaAluno5() {
        // Act
        List<String> recomendacoes = service.getRecommendations(5L);

        // Assert
        assertEquals(2, recomendacoes.size());
        assertTrue(recomendacoes.contains("Tecnologia - Testes"));
        assertTrue(recomendacoes.contains("Tecnologia - QA"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para aluno não existente")
    void deveRetornarListaVaziaParaAlunoInexistente() {
        // Act
        List<String> recomendacoes = service.getRecommendations(999L);

        // Assert
        assertNotNull(recomendacoes);
        assertTrue(recomendacoes.isEmpty());
    }

    // ========== Testes do Integrante 2: Kevyn Rocha ==========

    @Test
    @DisplayName("Deve enviar email com email válido e recomendações existentes")
    void deveEnviarEmailComEmailValidoERecomendacoesExistentes() {
        // Act
        boolean enviado = service.sendRecommendationEmail(1L, "aluno@exemplo.com");

        // Assert
        assertTrue(enviado);
    }

    @Test
    @DisplayName("Não deve enviar email com email inválido (sem @)")
    void naoDeveEnviarEmailSemArroba() {
        // Act
        boolean enviado = service.sendRecommendationEmail(1L, "emailinvalido.com");

        // Assert
        assertFalse(enviado);
    }

    @Test
    @DisplayName("Não deve enviar email com email inválido (sem domínio)")
    void naoDeveEnviarEmailSemDominio() {
        // Act
        boolean enviado = service.sendRecommendationEmail(1L, "email@");

        // Assert
        assertFalse(enviado);
    }

    @Test
    @DisplayName("Não deve enviar email com email inválido (sem extensão)")
    void naoDeveEnviarEmailSemExtensao() {
        // Act
        boolean enviado = service.sendRecommendationEmail(1L, "email@dominio");

        // Assert
        assertFalse(enviado);
    }

    @Test
    @DisplayName("Não deve enviar email quando email é null")
    void naoDeveEnviarEmailQuandoEmailNull() {
        // Act
        boolean enviado = service.sendRecommendationEmail(1L, null);

        // Assert
        assertFalse(enviado);
    }

    @Test
    @DisplayName("Não deve enviar email quando aluno não tem recomendações")
    void naoDeveEnviarEmailQuandoAlunoSemRecomendacoes() {
        // Act
        boolean enviado = service.sendRecommendationEmail(999L, "aluno@exemplo.com");

        // Assert
        assertFalse(enviado);
    }

    @Test
    @DisplayName("Deve enviar email com diferentes formatos válidos")
    void deveEnviarEmailComDiferentesFormatosValidos() {
        // Act & Assert
        assertTrue(service.sendRecommendationEmail(1L, "teste@teste.com"));
        assertTrue(service.sendRecommendationEmail(1L, "usuario.nome@dominio.com.br"));
        assertTrue(service.sendRecommendationEmail(1L, "email123@sub.dominio.org"));
    }

    @Test
    @DisplayName("Não deve enviar email com espaços")
    void naoDeveEnviarEmailComEspacos() {
        // Act
        boolean enviado = service.sendRecommendationEmail(1L, "email @exemplo.com");

        // Assert
        assertFalse(enviado);
    }

    // ========== Testes do Integrante 3: José Antônio ==========

    @Test
    @DisplayName("Deve filtrar recomendações por categoria Tecnologia")
    void deveFiltrarRecomendacoesPorCategoriaTecnologia() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(1L, "Tecnologia");

        // Assert
        assertEquals(2, filtradas.size());
        assertTrue(filtradas.stream().allMatch(r -> r.contains("Tecnologia")));
    }

    @Test
    @DisplayName("Deve filtrar recomendações por subcategoria DevOps")
    void deveFiltrarRecomendacoesPorSubcategoriaDevOps() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(1L, "DevOps");

        // Assert
        assertEquals(1, filtradas.size());
        assertTrue(filtradas.get(0).contains("DevOps"));
    }

    @Test
    @DisplayName("Deve filtrar recomendações por categoria Gestão")
    void deveFiltrarRecomendacoesPorCategoriaGestao() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(2L, "Gestão");

        // Assert
        assertEquals(1, filtradas.size());
        assertTrue(filtradas.get(0).contains("Gestão"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando categoria não existe")
    void deveRetornarListaVaziaQuandoCategoriaInexistente() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(1L, "Inexistente");

        // Assert
        assertTrue(filtradas.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar todas recomendações quando categoria é null")
    void deveRetornarTodasRecomendacoesQuandoCategoriaNull() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(1L, null);

        // Assert
        assertEquals(2, filtradas.size());
    }

    @Test
    @DisplayName("Deve retornar todas recomendações quando categoria é vazia")
    void deveRetornarTodasRecomendacoesQuandoCategoriaVazia() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(1L, "");

        // Assert
        assertEquals(2, filtradas.size());
    }

    @Test
    @DisplayName("Deve filtrar por palavra parcial")
    void deveFiltrarPorPalavraParcial() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(1L, "Curso");

        // Assert
        assertEquals(2, filtradas.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para aluno sem recomendações ao filtrar")
    void deveRetornarListaVaziaParaAlunoSemRecomendacoesAoFiltrar() {
        // Act
        List<String> filtradas = service.filterRecommendationsByCategory(999L, "Tecnologia");

        // Assert
        assertTrue(filtradas.isEmpty());
    }

    // ========== Testes do Integrante 4: Henry Santurião ==========

    @Test
    @DisplayName("Deve salvar curso para ver depois com sucesso")
    void deveSalvarCursoParaVerDepois() {
        // Act
        boolean salvo = service.saveRecommendationForLater(1L, "Curso de Java Avançado");

        // Assert
        assertTrue(salvo);
    }

    @Test
    @DisplayName("Deve salvar múltiplos cursos para ver depois")
    void deveSalvarMultiplosCursosParaVerDepois() {
        // Act
        boolean salvo1 = service.saveRecommendationForLater(1L, "Curso de Python");
        boolean salvo2 = service.saveRecommendationForLater(1L, "Curso de JavaScript");
        boolean salvo3 = service.saveRecommendationForLater(1L, "Curso de React");

        // Assert
        assertTrue(salvo1);
        assertTrue(salvo2);
        assertTrue(salvo3);
    }

    @Test
    @DisplayName("Não deve salvar curso quando nome é null")
    void naoDeveSalvarCursoQuandoNomeNull() {
        // Act
        boolean salvo = service.saveRecommendationForLater(1L, null);

        // Assert
        assertFalse(salvo);
    }

    @Test
    @DisplayName("Não deve salvar curso quando nome é vazio")
    void naoDeveSalvarCursoQuandoNomeVazio() {
        // Act
        boolean salvo = service.saveRecommendationForLater(1L, "");

        // Assert
        assertFalse(salvo);
    }

    @Test
    @DisplayName("Não deve salvar curso quando nome é apenas espaços")
    void naoDeveSalvarCursoQuandoNomeApenasEspacos() {
        // Act
        boolean salvo = service.saveRecommendationForLater(1L, "   ");

        // Assert
        assertFalse(salvo);
    }

    @Test
    @DisplayName("Deve permitir mesmo curso salvo por alunos diferentes")
    void devePermitirMesmoCursoSalvoPorAlunosDiferentes() {
        // Act
        boolean salvo1 = service.saveRecommendationForLater(1L, "Curso de DevOps");
        boolean salvo2 = service.saveRecommendationForLater(2L, "Curso de DevOps");

        // Assert
        assertTrue(salvo1);
        assertTrue(salvo2);
    }

    @Test
    @DisplayName("Deve salvar curso mesmo que já esteja salvo (Set não duplica)")
    void deveSalvarCursoMesmoQueJaEstejaSalvo() {
        // Act
        boolean salvo1 = service.saveRecommendationForLater(1L, "Curso X");
        boolean salvo2 = service.saveRecommendationForLater(1L, "Curso X");

        // Assert
        assertTrue(salvo1);
        assertTrue(salvo2);
    }

    // ========== Testes do Integrante 5: Luis Augusto ==========

    @Test
    @DisplayName("Deve marcar recomendação como útil com sucesso")
    void deveMarcarRecomendacaoComoUtil() {
        // Act
        boolean marcado = service.markRecommendationAsUseful(1L, "Tecnologia - Curso de DevOps");

        // Assert
        assertTrue(marcado);
    }

    @Test
    @DisplayName("Deve marcar múltiplas recomendações como úteis")
    void deveMarcarMultiplasRecomendacoesComoUteis() {
        // Act
        boolean marcado1 = service.markRecommendationAsUseful(1L, "Curso A");
        boolean marcado2 = service.markRecommendationAsUseful(1L, "Curso B");
        boolean marcado3 = service.markRecommendationAsUseful(1L, "Curso C");

        // Assert
        assertTrue(marcado1);
        assertTrue(marcado2);
        assertTrue(marcado3);
    }

    @Test
    @DisplayName("Não deve marcar quando curso é null")
    void naoDeveMarcarQuandoCursoNull() {
        // Act
        boolean marcado = service.markRecommendationAsUseful(1L, null);

        // Assert
        assertFalse(marcado);
    }

    @Test
    @DisplayName("Não deve marcar quando curso é vazio")
    void naoDeveMarcarQuandoCursoVazio() {
        // Act
        boolean marcado = service.markRecommendationAsUseful(1L, "");

        // Assert
        assertFalse(marcado);
    }

    @Test
    @DisplayName("Não deve marcar quando curso é apenas espaços")
    void naoDeveMarcarQuandoCursoApenasEspacos() {
        // Act
        boolean marcado = service.markRecommendationAsUseful(1L, "    ");

        // Assert
        assertFalse(marcado);
    }

    @Test
    @DisplayName("Deve permitir mesmo curso marcado por alunos diferentes")
    void devePermitirMesmoCursoMarcadoPorAlunosDiferentes() {
        // Act
        boolean marcado1 = service.markRecommendationAsUseful(1L, "Curso Popular");
        boolean marcado2 = service.markRecommendationAsUseful(2L, "Curso Popular");

        // Assert
        assertTrue(marcado1);
        assertTrue(marcado2);
    }

    @Test
    @DisplayName("Deve marcar curso mesmo que já esteja marcado (Set não duplica)")
    void deveMarcarCursoMesmoQueJaEstejaMarcado() {
        // Act
        boolean marcado1 = service.markRecommendationAsUseful(1L, "Curso Y");
        boolean marcado2 = service.markRecommendationAsUseful(1L, "Curso Y");

        // Assert
        assertTrue(marcado1);
        assertTrue(marcado2);
    }

    @Test
    @DisplayName("Deve funcionar para diferentes alunos independentemente")
    void deveFuncionarParaDiferentesAlunosIndependentemente() {
        // Act
        boolean marcado1 = service.markRecommendationAsUseful(1L, "Curso 1");
        boolean marcado2 = service.markRecommendationAsUseful(2L, "Curso 2");
        boolean marcado3 = service.markRecommendationAsUseful(3L, "Curso 3");

        // Assert
        assertTrue(marcado1);
        assertTrue(marcado2);
        assertTrue(marcado3);
    }
}