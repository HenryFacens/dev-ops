package br.com.valueprojects.mock_spring.test;

import br.com.valueprojects.mock_spring.model.Pagamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Pagamento")
class PagamentoTest {

    @Test
    @DisplayName("Deve criar pagamento com valor e data")
    void deveCriarPagamentoComValorEData() {
        Calendar data = Calendar.getInstance();
        Pagamento pagamento = new Pagamento(100.50, data);

        assertEquals(100.50, pagamento.getValor());
        assertEquals(data, pagamento.getData());
    }

    @Test
    @DisplayName("Deve criar pagamento com valor zero")
    void deveCriarPagamentoComValorZero() {
        Calendar data = Calendar.getInstance();
        Pagamento pagamento = new Pagamento(0.0, data);

        assertEquals(0.0, pagamento.getValor());
    }

    @Test
    @DisplayName("Deve criar pagamento com valor negativo")
    void deveCriarPagamentoComValorNegativo() {
        Calendar data = Calendar.getInstance();
        Pagamento pagamento = new Pagamento(-50.0, data);

        assertEquals(-50.0, pagamento.getValor());
    }

    @Test
    @DisplayName("Deve criar pagamento com data específica")
    void deveCriarPagamentoComDataEspecifica() {
        Calendar data = Calendar.getInstance();
        data.set(2025, Calendar.MARCH, 15);

        Pagamento pagamento = new Pagamento(250.75, data);

        assertEquals(2025, pagamento.getData().get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, pagamento.getData().get(Calendar.MONTH));
    }
}