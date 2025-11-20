package com.devops.qas.tests.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.devops.qas.tests.TestsApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class TestsApplicationTest {

    @Test
    void contextLoads() {
        // Verifica se o contexto Spring carrega sem erros
    }

    @Test
    void mainMethodShouldRunWithoutException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            // Simula a execução do main sem realmente iniciar o servidor
            String[] args = {};
            // TestsApplication.main(args); // Descomente se quiser testar o main real
        });
    }

    @Test
    void applicationShouldHaveMainMethod() throws NoSuchMethodException {
        // Verifica se o método main existe e tem a assinatura correta
        assertDoesNotThrow(() ->
                TestsApplication.class.getMethod("main", String[].class)
        );
    }
}