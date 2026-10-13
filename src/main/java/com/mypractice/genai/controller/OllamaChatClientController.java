package com.mypractice.genai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/ollama/chat")
public class OllamaChatClientController {

    private static final String SYSTEM_PROMPT = """
            You are helpful assistance that drafts professional and concise email base on input
            Ensure the emails are clear, polite and tailored to the specific context.
            User formal & respect full tone.
            """;

    private final ChatClient chatClient;

    private static final String AI_SYSTEM_PROMPT= """
            You are assistance that share professional and concise details, which use less token
            """;

    public OllamaChatClientController(@Qualifier("ollamaAIChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/draft-email")
    public String draftEmail(@RequestBody String message){
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call().content();
    }

    @PostMapping("/ai-revolution")
    public String aiRevolution(@RequestBody String message) {

        return chatClient.prompt()
                .system(AI_SYSTEM_PROMPT)
                .user(message)

                .options(
                        OllamaChatOptions.builder()
                                .maxTokens(100)
                                .topP(0.7)
                                .topK(5)
                )
                .call()
                .content();
    }
}
