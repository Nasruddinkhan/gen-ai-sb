package com.mypractice.genai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean("openAIChatClient")
    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel,
                                SimpleLoggerAdvisor simpleLoggerAdvisor,
                                SafeGuardAdvisor safeGuardAdvisor){
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(simpleLoggerAdvisor, safeGuardAdvisor)
                .build();
    }

    @Bean("ollamaAIChatClient")
    ChatClient ollamaAIChatClient(OllamaChatModel ollamaChatModel,
                                  SimpleLoggerAdvisor simpleLoggerAdvisor,
                                  SafeGuardAdvisor safeGuardAdvisor){
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(simpleLoggerAdvisor, safeGuardAdvisor)
                .build();

    }

    @Bean("simpleLoggerAdvisor")
    public SimpleLoggerAdvisor simpleLoggerAdvisor(){
        return SimpleLoggerAdvisor.builder().build();
    }

    @Bean("safeGuardAdvisor")
    public SafeGuardAdvisor safeGuardAdvisor(){
        return  SafeGuardAdvisor.builder()
                .sensitiveWords(List.of("password", "username"))
                .failureResponse("Your request contains restricted information.")
                .build();
    }
}
