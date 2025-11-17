package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Jogo")
class JogoTest {

    @Test
    @DisplayName("Deve criar jogo com descrição")
    void deveCriarJogoComDescricao() {
        Jogo jogo = new Jogo("Futebol");
        assertEquals("Futebol", jogo.getDescricao());
        assertNotNull(jogo.getData());
        assertFalse(jogo.isFinalizado());
    }

    @Test
    @DisplayName("Deve criar jogo com data específica")
    void deveCriarJogoComDataEspecifica() {
        Calendar data = Calendar.getInstance();
        data.set(2025, Calendar.JANUARY, 15);

        Jogo jogo = new Jogo("Basquete", data);

        assertEquals("Basquete", jogo.getDescricao());
        assertNotNull(jogo.getData());
    }

    @Test
    @DisplayName("Deve anotar resultado")
    void deveAnotarResultado() {
        Jogo jogo = new Jogo("Tênis");
        Participante p = new Participante("João");
        Resultado r = new Resultado(p, 100.0);

        jogo.anota(r);

        assertEquals(1, jogo.getResultados().size());
    }

    @Test
    @DisplayName("Deve anotar múltiplos resultados de participantes diferentes")
    void deveAnotarMultiplosResultadosDeDiferentes() {
        Jogo jogo = new Jogo("Corrida");
        Participante p1 = new Participante("Ana");
        Participante p2 = new Participante("Bruno");

        jogo.anota(new Resultado(p1, 80.0));
        jogo.anota(new Resultado(p2, 90.0));

        assertEquals(2, jogo.getResultados().size());
    }

    @Test
    @DisplayName("Não deve anotar resultado duplicado consecutivo")
    void naoDeveAnotarResultadoDuplicadoConsecutivo() {
        Jogo jogo = new Jogo("Vôlei");
        Participante p = new Participante("Carlos");

        jogo.anota(new Resultado(p, 70.0));
        jogo.anota(new Resultado(p, 75.0)); // Mesmo participante consecutivo

        assertEquals(1, jogo.getResultados().size());
    }

    @Test
    @DisplayName("Deve anotar até 5 resultados do mesmo participante")
    void deveAnotarAte5ResultadosDoMesmoParticipante() {
        Jogo jogo = new Jogo("Torneio");
        Participante p1 = new Participante("Pedro");
        Participante p2 = new Participante("Maria");

        jogo.anota(new Resultado(p1, 10.0));
        jogo.anota(new Resultado(p2, 20.0));
        jogo.anota(new Resultado(p1, 30.0));
        jogo.anota(new Resultado(p2, 40.0));
        jogo.anota(new Resultado(p1, 50.0));
        jogo.anota(new Resultado(p2, 60.0));
        jogo.anota(new Resultado(p1, 70.0));
        jogo.anota(new Resultado(p2, 80.0));
        jogo.anota(new Resultado(p1, 90.0));
        jogo.anota(new Resultado(p2, 100.0)); // 5º de p2
        jogo.anota(new Resultado(p1, 110.0)); // 6º de p1 - não deve adicionar

        // p1 deve ter 5 resultados, p2 deve ter 5 resultados
        long countP1 = jogo.getResultados().stream()
                .filter(r -> r.getParticipante().equals(p1))
                .count();
        assertEquals(5, countP1);
    }

    @Test
    @DisplayName("Deve finalizar jogo")
    void deveFinalizarJogo() {
        Jogo jogo = new Jogo("Final");
        assertFalse(jogo.isFinalizado());

        jogo.finaliza();

        assertTrue(jogo.isFinalizado());
    }

    @Test
    @DisplayName("Deve retornar lista imutável de resultados")
    void deveRetornarListaImutavelDeResultados() {
        Jogo jogo = new Jogo("Teste");
        jogo.anota(new Resultado(new Participante("A"), 50.0));

        assertThrows(UnsupportedOperationException.class, () -> {
            jogo.getResultados().add(new Resultado(new Participante("B"), 60.0));
        });
    }

    @Test
    @DisplayName("Deve retornar cópia da data")
    void deveRetornarCopiaDaData() {
        Jogo jogo = new Jogo("Clone Test");
        Calendar data1 = jogo.getData();
        Calendar data2 = jogo.getData();

        assertNotSame(data1, data2);
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        Jogo jogo = new Jogo("ID Test");
        jogo.setId(42);

        assertEquals(42, jogo.getId());
    }

    @Test
    @DisplayName("Deve definir data")
    void deveDefinirData() {
        Jogo jogo = new Jogo("Data Test");
        Calendar novaData = Calendar.getInstance();
        novaData.set(2024, Calendar.DECEMBER, 25);

        jogo.setData(novaData);

        assertEquals(novaData.get(Calendar.YEAR), jogo.getData().get(Calendar.YEAR));
        assertEquals(novaData.get(Calendar.MONTH), jogo.getData().get(Calendar.MONTH));
        assertEquals(novaData.get(Calendar.DAY_OF_MONTH), jogo.getData().get(Calendar.DAY_OF_MONTH));
    }
}