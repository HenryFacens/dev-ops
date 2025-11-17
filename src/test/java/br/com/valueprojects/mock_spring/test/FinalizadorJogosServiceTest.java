package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import br.com.valueprojects.mock_spring.service.FinalizadorJogosService;
import br.com.valueprojects.mock_spring.service.SMSService;
import infra.JogoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinalizadorJogosServiceTest {

    private JogoDao jogoDao;
    private SMSService smsService;
    private FinalizadorJogosService service;

    @BeforeEach
    void setup() {
        jogoDao = mock(JogoDao.class);
        smsService = mock(SMSService.class);
        service = new FinalizadorJogosService(jogoDao, smsService);
    }

    @Test
    void deveFinalizarJogosDaSemanaAnterior() {
        // Arrange
        Calendar seteDiasAtras = Calendar.getInstance();
        seteDiasAtras.add(Calendar.DAY_OF_MONTH, -8);

        Participante participante1 = new Participante("João");
        Participante participante2 = new Participante("Maria");

        Jogo jogo = new Jogo("Jogo de Xadrez");
        jogo.setData(seteDiasAtras);
        jogo.anota(new Resultado(participante1, 100.0));
        jogo.anota(new Resultado(participante2, 80.0));

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(1, totalFinalizados);
        assertTrue(jogo.isFinalizado());
        verify(jogoDao, times(1)).salva(jogo);
        verify(smsService, times(1)).enviarMensagemVitoria(participante1, "Jogo de Xadrez");
    }

    @Test
    void naoDeveFinalizarJogosMuitoRecentes() {
        // Arrange
        Calendar hoje = Calendar.getInstance();

        Jogo jogo = new Jogo("Jogo Recente");
        jogo.setData(hoje);

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(0, totalFinalizados);
        assertFalse(jogo.isFinalizado());
        verify(jogoDao, never()).salva(any());
        verify(smsService, never()).enviarMensagemVitoria(any(), any());
    }

    @Test
    void naoDeveEnviarSMSQuandoNaoExistemJogosParaFinalizar() {
        // Arrange
        when(jogoDao.emAndamento()).thenReturn(Collections.emptyList());

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(0, totalFinalizados);
        verify(smsService, never()).enviarMensagemVitoria(any(), any());
    }

    @Test
    void naoDeveEnviarSMSQuandoJogoNaoTemVencedor() {
        // Arrange
        Calendar seteDiasAtras = Calendar.getInstance();
        seteDiasAtras.add(Calendar.DAY_OF_MONTH, -8);

        Jogo jogoSemResultados = new Jogo("Jogo sem resultados");
        jogoSemResultados.setData(seteDiasAtras);

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogoSemResultados));

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(1, totalFinalizados);
        verify(jogoDao, times(1)).salva(jogoSemResultados);
        verify(smsService, never()).enviarMensagemVitoria(any(), any());
    }

    @Test
    void deveSalvarJogoAntesDeEnviarSMS() {
        // Arrange
        Calendar seteDiasAtras = Calendar.getInstance();
        seteDiasAtras.add(Calendar.DAY_OF_MONTH, -10);

        Participante vencedor = new Participante("Carlos");

        Jogo jogo = new Jogo("Jogo Importante");
        jogo.setData(seteDiasAtras);
        jogo.anota(new Resultado(vencedor, 200.0));

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        service.finalizarJogosDaSemanaAnterior();

        // Assert
        // Verifica a ordem de chamadas
        var inOrder = inOrder(jogoDao, smsService);
        inOrder.verify(jogoDao).salva(jogo);
        inOrder.verify(smsService).enviarMensagemVitoria(vencedor, "Jogo Importante");
    }

    @Test
    void deveFinalizarMultiplosJogos() {
        // Arrange
        Calendar dezDiasAtras = Calendar.getInstance();
        dezDiasAtras.add(Calendar.DAY_OF_MONTH, -10);

        Participante p1 = new Participante("Ana");
        Participante p2 = new Participante("Bruno");

        Jogo jogo1 = new Jogo("Jogo 1");
        jogo1.setData(dezDiasAtras);
        jogo1.anota(new Resultado(p1, 150.0));

        Jogo jogo2 = new Jogo("Jogo 2");
        jogo2.setData(dezDiasAtras);
        jogo2.anota(new Resultado(p2, 180.0));

        when(jogoDao.emAndamento()).thenReturn(Arrays.asList(jogo1, jogo2));

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(2, totalFinalizados);
        verify(jogoDao, times(2)).salva(any(Jogo.class));
        verify(smsService, times(2)).enviarMensagemVitoria(any(), any());
    }

    @Test
    void deveIdentificarVencedorComMaiorMetrica() {
        // Arrange
        Calendar seteDiasAtras = Calendar.getInstance();
        seteDiasAtras.add(Calendar.DAY_OF_MONTH, -8);

        Participante perdedor = new Participante("Perdedor");
        Participante vencedor = new Participante("Vencedor");
        Participante segundo = new Participante("Segundo");

        Jogo jogo = new Jogo("Competição");
        jogo.setData(seteDiasAtras);
        jogo.anota(new Resultado(perdedor, 50.0));
        jogo.anota(new Resultado(vencedor, 300.0));  // Maior métrica
        jogo.anota(new Resultado(segundo, 200.0));

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        service.finalizarJogosDaSemanaAnterior();

        // Assert
        ArgumentCaptor<Participante> captor = ArgumentCaptor.forClass(Participante.class);
        verify(smsService).enviarMensagemVitoria(captor.capture(), eq("Competição"));
        assertEquals("Vencedor", captor.getValue().getNome());
    }

    @Test
    void deveCalcularDiasCorretamente() {
        // Arrange - Jogo com exatamente 7 dias
        Calendar seteDiasExatos = Calendar.getInstance();
        seteDiasExatos.add(Calendar.DAY_OF_MONTH, -7);

        Participante participante = new Participante("Teste");

        Jogo jogo = new Jogo("Jogo Limite");
        jogo.setData(seteDiasExatos);
        jogo.anota(new Resultado(participante, 100.0));

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(1, totalFinalizados);
        verify(jogoDao, times(1)).salva(jogo);
    }

    @Test
    void naoDeveFinalizarJogoComMenosDe7Dias() {
        // Arrange
        Calendar seisDiasAtras = Calendar.getInstance();
        seisDiasAtras.add(Calendar.DAY_OF_MONTH, -6);

        Jogo jogo = new Jogo("Jogo Recente");
        jogo.setData(seisDiasAtras);

        when(jogoDao.emAndamento()).thenReturn(Collections.singletonList(jogo));

        // Act
        int totalFinalizados = service.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(0, totalFinalizados);
        verify(jogoDao, never()).salva(any());
    }
}