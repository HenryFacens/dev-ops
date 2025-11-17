package infra;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes do JogoDao")
class JogoDaoTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private JogoDao dao;

    @Test
    @DisplayName("Deve lançar exceção quando não conseguir conectar ao banco")
    void deveLancarExcecaoQuandoNaoConseguirConectar() {
        // Arrange & Act & Assert
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenThrow(new SQLException("Erro de conexão"));

            assertThrows(RuntimeException.class, () -> new JogoDao());
        }
    }

    @Test
    @DisplayName("Deve criar conexão com banco de dados")
    void deveCriarConexaoComBancoDeDados() throws Exception {
        // Arrange
        Connection mockConn = mock(Connection.class);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection("jdbc:mysql://localhost/mocks", "root", "")
            ).thenReturn(mockConn);

            // Act & Assert
            assertDoesNotThrow(() -> new JogoDao());
        }
    }

    @Test
    @DisplayName("Deve salvar jogo sem resultados")
    void deveSalvarJogoSemResultados() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt(1)).thenReturn(1);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();
            Jogo jogo = new Jogo("Futebol");

            // Act
            dao.salva(jogo);

            // Assert
            verify(mockPreparedStatement).setString(1, "Futebol");
            verify(mockPreparedStatement).setBoolean(3, false);
            verify(mockPreparedStatement).execute();
            assertEquals(1, jogo.getId());
        }
    }

    @Test
    @DisplayName("Deve salvar jogo com resultados")
    void deveSalvarJogoComResultados() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        PreparedStatement mockPsJogo = mock(PreparedStatement.class);
        PreparedStatement mockPsResultado = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("INSERT INTO JOGO"), anyInt()))
                .thenReturn(mockPsJogo);
        when(mockConnection.prepareStatement(contains("INSERT INTO RESULTADOS")))
                .thenReturn(mockPsResultado);
        when(mockPsJogo.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt(1)).thenReturn(10);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();

            Participante p1 = new Participante(1, "João");
            Jogo jogo = new Jogo("Basquete");
            jogo.anota(new Resultado(p1, 100.0));

            // Act
            dao.salva(jogo);

            // Assert
            verify(mockPsResultado).setInt(1, 10);
            verify(mockPsResultado).setInt(2, 1);
            verify(mockPsResultado).setDouble(3, 100.0);
            verify(mockPsResultado).execute();
        }
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar ao salvar")
    void deveLancarExcecaoAoFalharAoSalvar() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString(), anyInt()))
                .thenThrow(new SQLException("Erro ao salvar"));

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();
            Jogo jogo = new Jogo("Teste");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> dao.salva(jogo));
        }
    }

    @Test
    @DisplayName("Deve buscar jogos finalizados")
    void deveBuscarJogosFinalizados() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        PreparedStatement mockPs2 = mock(PreparedStatement.class);
        ResultSet mockRs2 = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FINALIZADO = true")))
                .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(contains("RESULTADOS")))
                .thenReturn(mockPs2);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPs2.executeQuery()).thenReturn(mockRs2);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("descricao")).thenReturn("Jogo Finalizado");
        when(mockResultSet.getDate("data")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getBoolean("finalizado")).thenReturn(true);

        when(mockRs2.next()).thenReturn(false);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();

            // Act
            List<Jogo> finalizados = dao.finalizados();

            // Assert
            assertEquals(1, finalizados.size());
            assertTrue(finalizados.get(0).isFinalizado());
        }
    }

    @Test
    @DisplayName("Deve buscar jogos em andamento")
    void deveBuscarJogosEmAndamento() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        PreparedStatement mockPs2 = mock(PreparedStatement.class);
        ResultSet mockRs2 = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FINALIZADO = false")))
                .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(contains("RESULTADOS")))
                .thenReturn(mockPs2);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPs2.executeQuery()).thenReturn(mockRs2);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("descricao")).thenReturn("Jogo em Andamento");
        when(mockResultSet.getDate("data")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getInt("id")).thenReturn(2);
        when(mockResultSet.getBoolean("finalizado")).thenReturn(false);

        when(mockRs2.next()).thenReturn(false);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();

            // Act
            List<Jogo> emAndamento = dao.emAndamento();

            // Assert
            assertEquals(1, emAndamento.size());
            assertFalse(emAndamento.get(0).isFinalizado());
        }
    }

    @Test
    @DisplayName("Deve buscar jogos com resultados")
    void deveBuscarJogosComResultados() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        PreparedStatement mockPs2 = mock(PreparedStatement.class);
        ResultSet mockRs2 = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("FINALIZADO = false")))
                .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(contains("RESULTADOS")))
                .thenReturn(mockPs2);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPs2.executeQuery()).thenReturn(mockRs2);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("descricao")).thenReturn("Jogo com Resultados");
        when(mockResultSet.getDate("data")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getInt("id")).thenReturn(3);
        when(mockResultSet.getBoolean("finalizado")).thenReturn(false);

        when(mockRs2.next()).thenReturn(true).thenReturn(false);
        when(mockRs2.getInt("id")).thenReturn(1);
        when(mockRs2.getString("nome")).thenReturn("Jogador 1");
        when(mockRs2.getDouble("metrica")).thenReturn(95.5);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();

            // Act
            List<Jogo> jogos = dao.emAndamento();

            // Assert
            assertEquals(1, jogos.size());
            assertEquals(1, jogos.get(0).getResultados().size());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar buscar jogos")
    void deveLancarExcecaoAoFalharBuscarJogos() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erro na busca"));

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();

            // Act & Assert
            assertThrows(RuntimeException.class, () -> dao.emAndamento());
            assertThrows(RuntimeException.class, () -> dao.finalizados());
        }
    }

    @Test
    @DisplayName("Deve atualizar jogo")
    void deveAtualizarJogo() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(contains("UPDATE JOGO")))
                .thenReturn(mockPreparedStatement);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();
            Jogo jogo = new Jogo("Jogo Atualizado");
            jogo.setId(5);
            jogo.finaliza();

            // Act
            dao.atualiza(jogo);

            // Assert
            verify(mockPreparedStatement).setString(1, "Jogo Atualizado");
            verify(mockPreparedStatement).setBoolean(3, true);
            verify(mockPreparedStatement).setInt(4, 5);
            verify(mockPreparedStatement).execute();
        }
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar atualizar")
    void deveLancarExcecaoAoFalharAtualizar() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(contains("UPDATE")))
                .thenThrow(new SQLException("Erro ao atualizar"));

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();
            Jogo jogo = new Jogo("Teste");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> dao.atualiza(jogo));
        }
    }

    @Test
    @DisplayName("Método x deve retornar 10")
    void metodoXDeveRetornar10() throws Exception {
        // Arrange
        mockConnection = mock(Connection.class);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            dao = new JogoDao();

            // Act
            int resultado = dao.x();

            // Assert
            assertEquals(10, resultado);
        }
    }
}