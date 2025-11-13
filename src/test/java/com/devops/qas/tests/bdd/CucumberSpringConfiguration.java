package com.devops.qas.tests.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Configuração do contexto Spring para testes Cucumber BDD.
 * Esta classe conecta o Cucumber ao contexto do Spring Boot.
 */
@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {
    // Esta classe não precisa de implementação
    // Ela apenas fornece a configuração necessária para o Cucumber usar o Spring
}