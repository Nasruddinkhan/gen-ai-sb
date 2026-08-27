package com.mypractice.genai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean("openAIChatClient")
    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel){
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean("ollamaAIChatClient")
    ChatClient ollamaAIChatClient(OllamaChatModel ollamaChatModel){
        return ChatClient.builder(ollamaChatModel).build();

    }
}
