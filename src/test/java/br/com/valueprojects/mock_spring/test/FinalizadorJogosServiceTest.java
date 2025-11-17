package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.model.FinalizaJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import infra.JogoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes do FinalizaJogo")
class FinalizaJogoTest {

    private JogoDao dao;
    private FinalizaJogo finalizaJogo;

    @BeforeEach
    void setup() {
        dao = mock(JogoDao.class);
        finalizaJogo = new FinalizaJogo(dao);
    }

    @Test
    @DisplayName("Deve finalizar jogos da semana anterior")
    void deveFinalizarJogosDaSemanaAnterior() {
        // Arrange
        Calendar seteDiasAtras = Calendar.getInstance();
        seteDiasAtras.add(Calendar.DAY_OF_MONTH, -8);

        Jogo jogo = new Jogo("Jogo Antigo", seteDiasAtras);
        when(dao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertEquals(1, finalizaJogo.getTotalFinalizados());
        assertTrue(jogo.isFinalizado());
        verify(dao).atualiza(jogo);
    }

    @Test
    @DisplayName("Não deve finalizar jogos recentes")
    void naoDeveFinalizarJogosRecentes() {
        // Arrange
        Calendar hoje = Calendar.getInstance();
        Jogo jogo = new Jogo("Jogo Recente", hoje);
        when(dao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertEquals(0, finalizaJogo.getTotalFinalizados());
        assertFalse(jogo.isFinalizado());
        verify(dao, never()).atualiza(any());
    }

    @Test
    @DisplayName("Deve finalizar múltiplos jogos antigos")
    void deveFinalizarMultiplosJogosAntigos() {
        // Arrange
        Calendar dataBase = Calendar.getInstance();
        dataBase.add(Calendar.DAY_OF_MONTH, -10);

        Jogo jogo1 = new Jogo("Jogo 1", (Calendar) dataBase.clone());
        Jogo jogo2 = new Jogo("Jogo 2", (Calendar) dataBase.clone());

        when(dao.emAndamento()).thenReturn(Arrays.asList(jogo1, jogo2));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertEquals(2, finalizaJogo.getTotalFinalizados());
        verify(dao, times(2)).atualiza(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve retornar zero quando não há jogos")
    void deveRetornarZeroQuandoNaoHaJogos() {
        // Arrange
        when(dao.emAndamento()).thenReturn(Collections.emptyList());

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertEquals(0, finalizaJogo.getTotalFinalizados());
    }

    @Test
    @DisplayName("Deve finalizar apenas jogos com 7 ou mais dias")
    void deveFinalizarApenasJogosCom7OuMaisDias() {
        // Arrange - Usa a mesma data base para evitar problemas de timing
        Calendar dataBase = Calendar.getInstance();

        Calendar seteDias = (Calendar) dataBase.clone();
        seteDias.add(Calendar.DAY_OF_MONTH, -7);

        Calendar cincoDias = (Calendar) dataBase.clone();
        cincoDias.add(Calendar.DAY_OF_MONTH, -5);

        Jogo jogoAntigo = new Jogo("Antigo", seteDias);
        Jogo jogoRecente = new Jogo("Recente", cincoDias);

        when(dao.emAndamento()).thenReturn(Arrays.asList(jogoAntigo, jogoRecente));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertTrue(finalizaJogo.getTotalFinalizados() >= 1, "Deve finalizar pelo menos o jogo de 7 dias");
        assertTrue(jogoAntigo.isFinalizado(), "Jogo com 7 dias deve estar finalizado");
        assertFalse(jogoRecente.isFinalizado(), "Jogo com 5 dias não deve estar finalizado");
    }

    @Test
    @DisplayName("Deve finalizar jogo com exatamente 7 dias")
    void deveFinalizarJogoComExatamente7Dias() {
        // Arrange
        Calendar seteDiasAtras = Calendar.getInstance();
        seteDiasAtras.add(Calendar.DAY_OF_MONTH, -7);

        Jogo jogo = new Jogo("Jogo 7 dias", seteDiasAtras);
        when(dao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertTrue(finalizaJogo.getTotalFinalizados() >= 1);
        assertTrue(jogo.isFinalizado());
    }

    @Test
    @DisplayName("Não deve finalizar jogo com menos de 7 dias")
    void naoDeveFinalizarJogoComMenosDe7Dias() {
        // Arrange
        Calendar cincoDiasAtras = Calendar.getInstance();
        cincoDiasAtras.add(Calendar.DAY_OF_MONTH, -5);

        Jogo jogo = new Jogo("Jogo 5 dias", cincoDiasAtras);
        when(dao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertEquals(0, finalizaJogo.getTotalFinalizados());
        assertFalse(jogo.isFinalizado());
    }

    @Test
    @DisplayName("Deve incrementar total a cada jogo finalizado")
    void deveIncrementarTotalACadaJogoFinalizado() {
        // Arrange
        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -10);

        Jogo jogo1 = new Jogo("Jogo 1", (Calendar) dataAntiga.clone());
        Jogo jogo2 = new Jogo("Jogo 2", (Calendar) dataAntiga.clone());
        Jogo jogo3 = new Jogo("Jogo 3", (Calendar) dataAntiga.clone());

        when(dao.emAndamento()).thenReturn(Arrays.asList(jogo1, jogo2, jogo3));

        // Act
        finalizaJogo.finaliza();

        // Assert
        assertEquals(3, finalizaJogo.getTotalFinalizados());
        assertTrue(jogo1.isFinalizado());
        assertTrue(jogo2.isFinalizado());
        assertTrue(jogo3.isFinalizado());
    }
}