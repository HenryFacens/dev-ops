package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Juiz;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Juiz")
class JuizTest {

    private Juiz juiz;

    @BeforeEach
    void setup() {
        juiz = new Juiz();
    }

    @Test
    @DisplayName("Deve julgar jogo com múltiplos resultados")
    void deveJulgarJogoComMultiplosResultados() {
        Jogo jogo = new Jogo("Torneio");
        jogo.anota(new Resultado(new Participante("A"), 100.0));
        jogo.anota(new Resultado(new Participante("B"), 200.0));
        jogo.anota(new Resultado(new Participante("C"), 150.0));

        juiz.julga(jogo);

        assertEquals(200.0, juiz.getPrimeiroColocado());
        assertEquals(100.0, juiz.getUltimoColocado());
    }

    @Test
    @DisplayName("Deve julgar jogo com um resultado")
    void deveJulgarJogoComUmResultado() {
        Jogo jogo = new Jogo("Solo");
        jogo.anota(new Resultado(new Participante("Único"), 50.0));

        juiz.julga(jogo);

        assertEquals(50.0, juiz.getPrimeiroColocado());
        assertEquals(50.0, juiz.getUltimoColocado());
    }

    @Test
    @DisplayName("Deve lançar exceção ao julgar jogo sem resultados")
    void deveLancarExcecaoAoJulgarJogoSemResultados() {
        Jogo jogo = new Jogo("Vazio");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            juiz.julga(jogo);
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Deve julgar jogo com valores negativos")
    void deveJulgarJogoComValoresNegativos() {
        Jogo jogo = new Jogo("Negativo");
        jogo.anota(new Resultado(new Participante("A"), -10.0));
        jogo.anota(new Resultado(new Participante("B"), -5.0));

        juiz.julga(jogo);

        assertEquals(-5.0, juiz.getPrimeiroColocado());
        assertEquals(-10.0, juiz.getUltimoColocado());
    }

    @Test
    @DisplayName("Deve retornar valores iniciais antes de julgar")
    void deveRetornarValoresIniciaisAntesDeJulgar() {
        assertEquals(Double.NEGATIVE_INFINITY, juiz.getPrimeiroColocado());
        assertEquals(Double.POSITIVE_INFINITY, juiz.getUltimoColocado());
    }

    @Test
    @DisplayName("Deve julgar jogo com resultados iguais")
    void deveJulgarJogoComResultadosIguais() {
        Jogo jogo = new Jogo("Empate");
        jogo.anota(new Resultado(new Participante("A"), 100.0));
        jogo.anota(new Resultado(new Participante("B"), 100.0));

        juiz.julga(jogo);

        assertEquals(100.0, juiz.getPrimeiroColocado());
        assertEquals(100.0, juiz.getUltimoColocado());
    }
}