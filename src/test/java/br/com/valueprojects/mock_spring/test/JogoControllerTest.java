package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.controller.JogoController;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JogoControllerTest {

    private JogoController controller;

    @BeforeEach
    void setup() {
        controller = new JogoController();
    }

    @Test
    void deveCriarNovoJogo() {
        // Act
        Jogo jogo = controller.criarJogo("Futebol");

        // Assert
        assertNotNull(jogo);
        assertEquals("Futebol", jogo.getDescricao());
        assertEquals(1, controller.listarJogos().size());
    }

    @Test
    void deveCriarMultiplosJogos() {
        // Act
        controller.criarJogo("Futebol");
        controller.criarJogo("Basquete");
        controller.criarJogo("Vôlei");

        // Assert
        List<Jogo> jogos = controller.listarJogos();
        assertEquals(3, jogos.size());
    }

    @Test
    void deveListarJogosVazioInicial() {
        // Act
        List<Jogo> jogos = controller.listarJogos();

        // Assert
        assertNotNull(jogos);
        assertTrue(jogos.isEmpty());
    }

    @Test
    void deveListarTodosOsJogos() {
        // Arrange
        controller.criarJogo("Jogo 1");
        controller.criarJogo("Jogo 2");

        // Act
        List<Jogo> jogos = controller.listarJogos();

        // Assert
        assertEquals(2, jogos.size());
        assertEquals("Jogo 1", jogos.get(0).getDescricao());
        assertEquals("Jogo 2", jogos.get(1).getDescricao());
    }

    @Test
    void deveJulgarJogo() {
        // Arrange
        Jogo jogo = controller.criarJogo("Torneio");

        Participante p1 = new Participante("João");
        Participante p2 = new Participante("Maria");
        Participante p3 = new Participante("Pedro");

        jogo.anota(new Resultado(p1, 100.0));
        jogo.anota(new Resultado(p2, 200.0));
        jogo.anota(new Resultado(p3, 150.0));

        // Act
        String resultado = controller.julgarJogo(0);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.contains("200.0")); // Primeiro colocado
        assertTrue(resultado.contains("100.0")); // Último colocado
    }

    @Test
    void deveJulgarJogoComUmParticipante() {
        // Arrange
        Jogo jogo = controller.criarJogo("Solo");
        Participante p1 = new Participante("Único");
        jogo.anota(new Resultado(p1, 50.0));

        // Act
        String resultado = controller.julgarJogo(0);

        // Assert
        assertTrue(resultado.contains("50.0"));
    }

    @Test
    void deveJulgarMultiplosJogos() {
        // Arrange
        Jogo jogo1 = controller.criarJogo("Jogo 1");
        jogo1.anota(new Resultado(new Participante("A"), 100.0));

        Jogo jogo2 = controller.criarJogo("Jogo 2");
        jogo2.anota(new Resultado(new Participante("B"), 200.0));

        // Act
        String resultado1 = controller.julgarJogo(0);
        String resultado2 = controller.julgarJogo(1);

        // Assert
        assertTrue(resultado1.contains("100.0"));
        assertTrue(resultado2.contains("200.0"));
    }

    @Test
    void deveRetornarJogoRecemCriado() {
        // Act
        Jogo jogo = controller.criarJogo("Novo Jogo");

        // Assert
        assertEquals(jogo, controller.listarJogos().get(0));
    }
}