package infra;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do JogoDaoFalso")
class JogoDaoFalsoTest {

    private JogoDaoFalso dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new JogoDaoFalso();

        // Limpa a lista estática usando reflection
        Field jogosField = JogoDaoFalso.class.getDeclaredField("Jogos");
        jogosField.setAccessible(true);
        List<Jogo> jogos = (List<Jogo>) jogosField.get(null);
        jogos.clear();
    }

    @Test
    @DisplayName("Deve salvar um jogo com sucesso")
    void deveSalvarJogoComSucesso() {
        // Arrange
        Jogo jogo = new Jogo("Futebol");

        // Act
        dao.salva(jogo);

        // Assert
        List<Jogo> emAndamento = dao.emAndamento();
        assertEquals(1, emAndamento.size());
        assertEquals("Futebol", emAndamento.get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve salvar múltiplos jogos")
    void deveSalvarMultiplosJogos() {
        // Arrange
        Jogo jogo1 = new Jogo("Basquete");
        Jogo jogo2 = new Jogo("Vôlei");
        Jogo jogo3 = new Jogo("Tênis");

        // Act
        dao.salva(jogo1);
        dao.salva(jogo2);
        dao.salva(jogo3);

        // Assert
        List<Jogo> emAndamento = dao.emAndamento();
        assertEquals(3, emAndamento.size());
    }

    @Test
    @DisplayName("Deve retornar apenas jogos finalizados")
    void deveRetornarApenasJogosFinalizados() {
        // Arrange
        Jogo jogoFinalizado1 = new Jogo("Jogo 1");
        jogoFinalizado1.finaliza();

        Jogo jogoFinalizado2 = new Jogo("Jogo 2");
        jogoFinalizado2.finaliza();

        Jogo jogoEmAndamento = new Jogo("Jogo 3");

        dao.salva(jogoFinalizado1);
        dao.salva(jogoFinalizado2);
        dao.salva(jogoEmAndamento);

        // Act
        List<Jogo> finalizados = dao.finalizados();

        // Assert
        assertEquals(2, finalizados.size());
        assertTrue(finalizados.stream().allMatch(Jogo::isFinalizado));
    }

    @Test
    @DisplayName("Deve retornar apenas jogos em andamento")
    void deveRetornarApenasJogosEmAndamento() {
        // Arrange
        Jogo jogoAndamento1 = new Jogo("Jogo A");
        Jogo jogoAndamento2 = new Jogo("Jogo B");
        Jogo jogoFinalizado = new Jogo("Jogo C");
        jogoFinalizado.finaliza();

        dao.salva(jogoAndamento1);
        dao.salva(jogoAndamento2);
        dao.salva(jogoFinalizado);

        // Act
        List<Jogo> emAndamento = dao.emAndamento();

        // Assert
        assertEquals(2, emAndamento.size());
        assertTrue(emAndamento.stream().noneMatch(Jogo::isFinalizado));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há jogos finalizados")
    void deveRetornarListaVaziaQuandoNaoHaJogosFinalizados() {
        // Arrange
        Jogo jogo = new Jogo("Jogo em andamento");
        dao.salva(jogo);

        // Act
        List<Jogo> finalizados = dao.finalizados();

        // Assert
        assertTrue(finalizados.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há jogos em andamento")
    void deveRetornarListaVaziaQuandoNaoHaJogosEmAndamento() {
        // Arrange
        Jogo jogo = new Jogo("Jogo finalizado");
        jogo.finaliza();
        dao.salva(jogo);

        // Act
        List<Jogo> emAndamento = dao.emAndamento();

        // Assert
        assertTrue(emAndamento.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar listas vazias quando não há jogos salvos")
    void deveRetornarListasVaziasQuandoNaoHaJogosSalvos() {
        // Act
        List<Jogo> finalizados = dao.finalizados();
        List<Jogo> emAndamento = dao.emAndamento();

        // Assert
        assertTrue(finalizados.isEmpty());
        assertTrue(emAndamento.isEmpty());
    }

    @Test
    @DisplayName("Deve salvar jogo com resultados")
    void deveSalvarJogoComResultados() {
        // Arrange
        Participante p1 = new Participante("João");
        Participante p2 = new Participante("Maria");

        Jogo jogo = new Jogo("Torneio");
        jogo.anota(new Resultado(p1, 100.0));
        jogo.anota(new Resultado(p2, 150.0));

        // Act
        dao.salva(jogo);

        // Assert
        List<Jogo> jogos = dao.emAndamento();
        assertEquals(1, jogos.size());
        assertEquals(2, jogos.get(0).getResultados().size());
    }

    @Test
    @DisplayName("Atualiza não deve fazer nada mas não deve lançar exceção")
    void atualizaNaoDeveFazerNadaMasNaoDeveLancarExcecao() {
        // Arrange
        Jogo jogo = new Jogo("Jogo para atualizar");
        jogo.setId(1);
        dao.salva(jogo);

        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> dao.atualiza(jogo));
    }

    @Test
    @DisplayName("Deve manter jogos após finalização")
    void deveManterJogosAposFinalizacao() {
        // Arrange
        Jogo jogo = new Jogo("Jogo Teste");
        dao.salva(jogo);

        // Act
        jogo.finaliza();

        // Assert
        assertEquals(0, dao.emAndamento().size());
        assertEquals(1, dao.finalizados().size());
    }

    @Test
    @DisplayName("Deve salvar jogo com data específica")
    void deveSalvarJogoComDataEspecifica() {
        // Arrange
        Calendar data = Calendar.getInstance();
        data.set(2025, Calendar.JANUARY, 15);

        Jogo jogo = new Jogo("Jogo Datado", data);

        // Act
        dao.salva(jogo);

        // Assert
        List<Jogo> jogos = dao.emAndamento();
        assertEquals(1, jogos.size());
        assertNotNull(jogos.get(0).getData());
    }

    @Test
    @DisplayName("Deve permitir salvar o mesmo jogo múltiplas vezes")
    void devePermitirSalvarMesmoJogoMultiplasVezes() {
        // Arrange
        Jogo jogo = new Jogo("Jogo Repetido");

        // Act
        dao.salva(jogo);
        dao.salva(jogo);
        dao.salva(jogo);

        // Assert
        assertEquals(3, dao.emAndamento().size());
    }
}