package com.devops.qas.tests.bdd.steps;

import com.devops.qas.tests.recommendation.RecommendationServiceStub;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class RecommendationSteps {

    private final RecommendationServiceStub service = new RecommendationServiceStub();
    private List<String> lista;
    private boolean resultadoBooleano;

    @Dado("que existe base de recomendações para os alunos")
    public void existeBase() {
        // Sem ação: a base está embutida no service/stub do seu projeto
    }

    @Quando("eu solicito recomendações para o aluno {string}")
    public void solicitoRecs(String alunoId) {
        lista = service.getRecommendations(Long.valueOf(alunoId));
    }

    @Então("a lista de recomendações não deve ser vazia")
    public void listaNaoVazia() {
        Assertions.assertNotNull(lista);
        Assertions.assertFalse(lista.isEmpty());
    }

    @Quando("eu envio recomendações por e-mail {string} para o aluno {string}")
    public void envioEmail(String email, String alunoId) {
        resultadoBooleano = service.sendRecommendationEmail(Long.valueOf(alunoId), email);
    }

    @Então("o envio deve retornar sucesso")
    public void envioSucesso() {
        Assertions.assertTrue(resultadoBooleano);
    }

    @Quando("eu filtro recomendações do aluno {string} pela categoria {string}")
    public void filtroPorCategoria(String alunoId, String categoria) {
        lista = service.filterRecommendationsByCategory(Long.valueOf(alunoId), categoria);
    }

    @Então("a lista filtrada não deve ser vazia")
    public void listaFiltradaNaoVazia() {
        Assertions.assertNotNull(lista);
        Assertions.assertFalse(lista.isEmpty());
    }

    @E("todos os itens devem conter {string}")
    public void itensContemCategoria(String termo) {
        Assertions.assertTrue(lista.stream().allMatch(s -> s.contains(termo)));
    }

    @Quando("eu salvo o curso {string} para o aluno {string}")
    public void salvarCurso(String curso, String alunoId) {
        resultadoBooleano = service.saveRecommendationForLater(Long.valueOf(alunoId), curso);
    }

    @Então("o retorno de salvar deve ser sucesso")
    public void salvarSucesso() {
        Assertions.assertTrue(resultadoBooleano);
    }

    @Quando("eu marco o curso {string} como útil para o aluno {string}")
    public void marcarUtil(String curso, String alunoId) {
        resultadoBooleano = service.markRecommendationAsUseful(Long.valueOf(alunoId), curso);
    }

    @Então("o retorno de útil deve ser sucesso")
    public void utilSucesso() {
        Assertions.assertTrue(resultadoBooleano);
    }

    @Quando("eu tento enviar recomendações por e-mail {string} para o aluno {string}")
    public void envioInvalido(String email, String alunoId) {
        String e = email == null || email.isBlank() ? null : email;
        resultadoBooleano = service.sendRecommendationEmail(Long.valueOf(alunoId), e);
    }

    @Então("o envio deve falhar")
    public void envioFalha() {
        Assertions.assertFalse(resultadoBooleano);
    }
}
