package com.devops.qas.tests.ai.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final ChatLanguageModel chatLanguageModel;

    public String generateRecommendation(String studentProfile) {
        log.info("Gerando recomendação com IA para perfil: {}", studentProfile);
        String prompt = String.format(
                "Com base no perfil do estudante: %s, gere uma recomendação personalizada de curso. " +
                "Seja conciso e objetivo.", studentProfile
        );
        String response = chatLanguageModel.generate(prompt);
        log.info("Resposta da IA: {}", response);
        return response;
    }

    public String chat(String userMessage) {
        log.info("Processando mensagem do usuário: {}", userMessage);
        String response = chatLanguageModel.generate(userMessage);
        log.info("Resposta da IA: {}", response);
        return response;
    }

    public String analyzeRecommendationFeedback(String feedback) {
        log.info("Analisando feedback com IA: {}", feedback);
        String prompt = String.format(
                "Analise o seguinte feedback sobre uma recomendação e forneça insights: %s", feedback
        );
        String response = chatLanguageModel.generate(prompt);
        log.info("Análise da IA: {}", response);
        return response;
    }
}

