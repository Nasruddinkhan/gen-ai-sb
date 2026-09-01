package com.mypractice.genai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/ollama/chat")
public class OllamaChatClientController {

    private static final String SYSTEM_PROMPT = """
            You are helpful assistance that drafts professional and concise email base on input
            Ensure the emails are clear, polite and tailored to the specific context.
            User formal & respect full tone.
            """;    private final ChatClient chatClient;

    public OllamaChatClientController(@Qualifier("openAIChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/draft-email")
    public String draftEmail(@RequestBody String message){
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call().content();
    }
}
