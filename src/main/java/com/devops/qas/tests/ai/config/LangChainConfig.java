package com.devops.qas.tests.ai.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChainConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name:gpt-3.5-turbo}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.temperature:0.7}")
    private Double temperature;

    @Value("${langchain4j.open-ai.chat-model.timeout:60s}")
    private String timeout;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        Duration timeoutDuration = parseTimeout(timeout);
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(timeoutDuration)
                .build();
    }

    private Duration parseTimeout(String timeoutStr) {
        if (timeoutStr.endsWith("s")) {
            int seconds = Integer.parseInt(timeoutStr.substring(0, timeoutStr.length() - 1));
            return Duration.ofSeconds(seconds);
        }
        return Duration.ofSeconds(60);
    }
}

