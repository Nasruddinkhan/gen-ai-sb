package com.mypractice.genai.config;

import com.mypractice.genai.advisor.DefaultSystemPromptAdvisor;
import com.mypractice.genai.advisor.PromptValidationAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean({"openAIChatClient", "chatClient"})
    @Profile({"openai", "docker", "huggingface"})
    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel,
                                PromptValidationAdvisor promptValidationAdvisor,
                                DefaultSystemPromptAdvisor defaultSystemPromptAdvisor,
                                SimpleLoggerAdvisor simpleLoggerAdvisor,
                                SafeGuardAdvisor safeGuardAdvisor){
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(promptValidationAdvisor, defaultSystemPromptAdvisor, simpleLoggerAdvisor, safeGuardAdvisor)
                .build();
    }

    @Bean({"ollamaAIChatClient", "chatClient"})
    @Profile("ollama")
    ChatClient ollamaAIChatClient(OllamaChatModel ollamaChatModel,
                                  PromptValidationAdvisor promptValidationAdvisor,
                                  DefaultSystemPromptAdvisor defaultSystemPromptAdvisor,
                                  SimpleLoggerAdvisor simpleLoggerAdvisor,
                                  SafeGuardAdvisor safeGuardAdvisor){
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(promptValidationAdvisor, defaultSystemPromptAdvisor, simpleLoggerAdvisor, safeGuardAdvisor)
                .build();

    }

    @Bean("promptValidationAdvisor")
    public PromptValidationAdvisor promptValidationAdvisor(){
        return new PromptValidationAdvisor(120);
    }

    @Bean("defaultSystemPromptAdvisor")
    public DefaultSystemPromptAdvisor defaultSystemPromptAdvisor(){
        return new DefaultSystemPromptAdvisor("""
                You are part of a Spring AI learning application.
                Keep responses helpful, clear, and beginner friendly.
                """);
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
