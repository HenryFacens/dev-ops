package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.model.Participante;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Participante")
class ParticipanteTest {

    @Test
    @DisplayName("Deve criar participante com nome")
    void deveCriarParticipanteComNome() {
        Participante participante = new Participante("João");

        assertNotNull(participante);
        assertEquals("João", participante.getNome());
        assertEquals(0, participante.getId());
    }

    @Test
    @DisplayName("Deve criar participante com id e nome")
    void deveCriarParticipanteComIdENome() {
        Participante participante = new Participante(1, "Maria");

        assertEquals(1, participante.getId());
        assertEquals("Maria", participante.getNome());
    }

    @Test
    @DisplayName("Deve retornar true para equals com mesmo objeto")
    void deveRetornarTrueParaEqualsComMesmoObjeto() {
        Participante p = new Participante(1, "Ana");

        assertTrue(p.equals(p));
    }

    @Test
    @DisplayName("Deve retornar false para equals com null")
    void deveRetornarFalseParaEqualsComNull() {
        Participante p = new Participante(1, "Bruno");

        assertFalse(p.equals(null));
    }

    @Test
    @DisplayName("Deve retornar false para equals com classe diferente")
    void deveRetornarFalseParaEqualsComClasseDiferente() {
        Participante p = new Participante(1, "Carlos");
        String outro = "Carlos";

        assertFalse(p.equals(outro));
    }

    @Test
    @DisplayName("Deve retornar true para participantes iguais")
    void deveRetornarTrueParaParticipantesIguais() {
        Participante p1 = new Participante(1, "Pedro");
        Participante p2 = new Participante(1, "Pedro");

        assertTrue(p1.equals(p2));
    }

    @Test
    @DisplayName("Deve retornar false para IDs diferentes")
    void deveRetornarFalseParaIDsDiferentes() {
        Participante p1 = new Participante(1, "João");
        Participante p2 = new Participante(2, "João");

        assertFalse(p1.equals(p2));
    }

    @Test
    @DisplayName("Deve retornar false para nomes diferentes")
    void deveRetornarFalseParaNomesDiferentes() {
        Participante p1 = new Participante(1, "Ana");
        Participante p2 = new Participante(1, "Maria");

        assertFalse(p1.equals(p2));
    }

    @Test
    @DisplayName("Deve retornar false quando um nome é null")
    void deveRetornarFalseQuandoUmNomeNull() {
        Participante p1 = new Participante(1, "João");
        Participante p2 = new Participante(1, null);

        assertFalse(p1.equals(p2));
    }

    @Test
    @DisplayName("Deve retornar true quando ambos nomes são null")
    void deveRetornarTrueQuandoAmbosNomesNull() {
        Participante p1 = new Participante(1, null);
        Participante p2 = new Participante(1, null);

        assertTrue(p1.equals(p2));
    }
}