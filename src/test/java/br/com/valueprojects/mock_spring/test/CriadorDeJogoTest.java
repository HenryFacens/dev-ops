package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.builder.CriadorDeJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class CriadorDeJogoTest {

    @Test
    void deveCriarJogoSimples() {
        // Act
        Jogo jogo = new CriadorDeJogo()
                .para("Xadrez")
                .constroi();

        // Assert
        assertNotNull(jogo);
        assertEquals("Xadrez", jogo.getDescricao());
    }

    @Test
    void deveCriarJogoComResultado() {
        // Arrange
        Participante participante = new Participante("João");

        // Act
        Jogo jogo = new CriadorDeJogo()
                .para("Tênis")
                .resultado(participante, 85.5)
                .constroi();

        // Assert
        assertNotNull(jogo);
        assertEquals(1, jogo.getResultados().size());
        assertEquals(85.5, jogo.getResultados().get(0).getMetrica());
    }

    @Test
    void deveCriarJogoComMultiplosResultados() {
        // Arrange
        Participante p1 = new Participante("Ana");
        Participante p2 = new Participante("Bruno");
        Participante p3 = new Participante("Carlos");

        // Act
        Jogo jogo = new CriadorDeJogo()
                .para("Corrida")
                .resultado(p1, 100.0)
                .resultado(p2, 90.0)
                .resultado(p3, 95.0)
                .constroi();

        // Assert
        assertEquals(3, jogo.getResultados().size());
    }

    @Test
    void deveCriarJogoComData() {
        // Arrange
        Calendar data = Calendar.getInstance();
        data.set(2025, Calendar.JANUARY, 15);

        // Act
        Jogo jogo = new CriadorDeJogo()
                .para("Natação")
                .naData(data)
                .constroi();

        // Assert
        assertNotNull(jogo.getData());
        assertEquals(data, jogo.getData());
    }

    @Test
    void deveCriarJogoFinalizado() {
        // Act
        CriadorDeJogo criador = new CriadorDeJogo()
                .para("Vôlei")
                .finaliza();

        // Assert
        assertTrue(criador.isFinalizado());
    }

    @Test
    void deveCriarJogoComId() {
        // Act
        CriadorDeJogo criador = new CriadorDeJogo()
                .para("Basquete")
                .comId(42);

        Jogo jogo = criador.constroi();

        // Assert
        assertEquals(42, criador.getId());
        assertEquals(42, jogo.getId());
    }

    @Test
    void deveCriarJogoCompleto() {
        // Arrange
        Calendar data = Calendar.getInstance();
        data.set(2025, Calendar.MARCH, 10);

        Participante p1 = new Participante("Time A");
        Participante p2 = new Participante("Time B");

        // Act
        CriadorDeJogo criador = new CriadorDeJogo()
                .para("Final do Campeonato")
                .comId(1)
                .naData(data)
                .resultado(p1, 3.0)
                .resultado(p2, 2.0)
                .finaliza();

        Jogo jogo = criador.constroi();

        // Assert
        assertNotNull(jogo);
        assertEquals("Final do Campeonato", jogo.getDescricao());
        assertEquals(1, jogo.getId());
        assertEquals(data, jogo.getData());
        assertEquals(2, jogo.getResultados().size());
        assertTrue(criador.isFinalizado());
    }

    @Test
    void devePermitirEncadeamentoDeMetodos() {
        // Act & Assert - verifica se o builder retorna this
        CriadorDeJogo criador = new CriadorDeJogo()
                .para("Teste")
                .comId(99)
                .finaliza();

        assertNotNull(criador);
        assertTrue(criador.isFinalizado());
        assertEquals(99, criador.getId());
    }

    @Test
    void deveRetornarIdCorreto() {
        // Act
        Jogo jogo = new CriadorDeJogo()
                .para("Jogo de Teste")
                .comId(123)
                .constroi();

        // Assert
        assertEquals(123, jogo.getId());
    }

    @Test
    void deveCriarJogoSemFinalizar() {
        // Act
        CriadorDeJogo criador = new CriadorDeJogo()
                .para("Jogo em Andamento");

        // Assert - não chamou finaliza()
        assertFalse(criador.isFinalizado());
    }
}