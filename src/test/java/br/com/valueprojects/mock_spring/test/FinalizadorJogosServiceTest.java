package br.com.valueprojects.mock_spring.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import br.com.valueprojects.mock_spring.service.FinalizadorJogosService;
import br.com.valueprojects.mock_spring.service.SMSService;
import infra.JogoDao;

public class FinalizadorJogosServiceTest {

    @Mock
    private JogoDao jogoDao;

    @Mock
    private SMSService smsService;

    private FinalizadorJogosService finalizadorService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        finalizadorService = new FinalizadorJogosService(jogoDao, smsService);
    }

    @Test
    public void deveFinalizarJogosDaSemanaAnterior() {
        // Arrange
        List<Jogo> jogosEmAndamento = criarJogosEmAndamentoComJogoAntigo();

        when(jogoDao.emAndamento()).thenReturn(jogosEmAndamento);

        // Act
        int jogosFinalizados = finalizadorService.finalizarJogosDaSemanaAnterior();
        // Assert
        assertEquals(1, jogosFinalizados);
        verify(jogoDao).salva(jogosEmAndamento.get(0)); // Verifica que o jogo foi salvo

        // Verifica que o SMS foi enviado para o vencedor (Participante 2 com pontuação 9.0)
        Participante vencedorEsperado = jogosEmAndamento.get(0).getResultados().get(1).getParticipante();
        verify(smsService).enviarMensagemVitoria(eq(vencedorEsperado), eq("Jogo Antigo"));
    }

    @Test
    public void deveSalvarJogoAntesDeEnviarSMS() {
        // Arrange
        List<Jogo> jogosEmAndamento = criarJogosEmAndamentoComJogoAntigo();
        when(jogoDao.emAndamento()).thenReturn(jogosEmAndamento);

        // Act
        finalizadorService.finalizarJogosDaSemanaAnterior();

        // Assert - Verificar a ordem das chamadas
        InOrder inOrder = inOrder(jogoDao, smsService);
        inOrder.verify(jogoDao).salva(any(Jogo.class)); // Verifica que o método salva foi chamado primeiro
        inOrder.verify(smsService).enviarMensagemVitoria(any(Participante.class), anyString()); // Depois o SMS
    }

    @Test
    public void naoDeveEnviarSMSQuandoNaoExistemJogosParaFinalizar() {
        // Arrange
        List<Jogo> jogosVazios = new ArrayList<>();
        when(jogoDao.emAndamento()).thenReturn(jogosVazios);

        // Act
        int jogosFinalizados = finalizadorService.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(0, jogosFinalizados);
        verifyNoInteractions(smsService); // Verifica que o serviço SMS não foi utilizado
    }

    @Test
    public void naoDeveFinalizarJogosMuitoRecentes() {
        // Arrange
        List<Jogo> jogosRecentes = criarJogosEmAndamentoRecentes();
        when(jogoDao.emAndamento()).thenReturn(jogosRecentes);

        // Act
        int jogosFinalizados = finalizadorService.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(0, jogosFinalizados);
        verify(jogoDao, never()).salva(any(Jogo.class)); // Verifica que o método salva nunca foi chamado
        verifyNoInteractions(smsService); // Verifica que o serviço SMS não foi utilizado
    }

    @Test
    public void naoDeveEnviarSMSQuandoJogoNaoTemVencedor() {
        // Arrange
        List<Jogo> jogosEmAndamento = criarJogosEmAndamentoComJogoAntigoSemResultados();
        when(jogoDao.emAndamento()).thenReturn(jogosEmAndamento);

        // Act
        int jogosFinalizados = finalizadorService.finalizarJogosDaSemanaAnterior();

        // Assert
        assertEquals(1, jogosFinalizados);
        verify(jogoDao).salva(jogosEmAndamento.get(0)); // Verifica que o jogo foi salvo
        verifyNoInteractions(smsService); // Mas o SMS não foi enviado
    }

    // Métodos auxiliares para criar cenários de teste

    private List<Jogo> criarJogosEmAndamentoComJogoAntigo() {
        List<Jogo> jogos = new ArrayList<>();

        // Cria um jogo antigo (mais de uma semana)
        Jogo jogoAntigo = new Jogo("Jogo Antigo");

        // Define a data do jogo para 10 dias atrás
        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -10);
        jogoAntigo.setData(dataAntiga);

        // Adiciona participantes e resultados
        Participante p1 = new Participante(1, "Participante 1");
        Participante p2 = new Participante(2, "Participante 2");

        jogoAntigo.anota(new Resultado(p1, 5.0));
        jogoAntigo.anota(new Resultado(p2, 9.0)); // Este será o vencedor

        jogos.add(jogoAntigo);
        return jogos;
    }

    private List<Jogo> criarJogosEmAndamentoRecentes() {
        List<Jogo> jogos = new ArrayList<>();

        // Cria um jogo recente (menos de uma semana)
        Jogo jogoRecente = new Jogo("Jogo Recente");

        // Define a data do jogo para 3 dias atrás
        Calendar dataRecente = Calendar.getInstance();
        dataRecente.add(Calendar.DAY_OF_MONTH, -3);
        jogoRecente.setData(dataRecente);

        jogos.add(jogoRecente);
        return jogos;
    }

    private List<Jogo> criarJogosEmAndamentoComJogoAntigoSemResultados() {
        List<Jogo> jogos = new ArrayList<>();

        // Cria um jogo antigo sem resultados
        Jogo jogoAntigo = new Jogo("Jogo Antigo Sem Resultados");

        // Define a data do jogo para 10 dias atrás
        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -10);
        jogoAntigo.setData(dataAntiga);

        // Não adiciona nenhum resultado

        jogos.add(jogoAntigo);
        return jogos;
    }
}
