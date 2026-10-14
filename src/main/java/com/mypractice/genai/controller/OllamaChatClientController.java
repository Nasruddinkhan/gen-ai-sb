package com.mypractice.genai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PostMapping("/json-response")
    public JsonChatResponse jsonResponse(@RequestBody JsonChatRequest request) {
        return chatClient.prompt()
                .system("Return the response mapped to the JsonChatResponse entity.")
                .user(request.message())
                .call()
                .entity(JsonChatResponse.class);
    }

    @PostMapping("/response-details")
    public AiResponseDetails responseDetails(@RequestBody JsonChatRequest request) {
        ChatResponse response = chatClient.prompt()
                .user(request.message())
                .call()
                .chatResponse();

        Generation result = response.getResult();
        AssistantMessage output = result != null ? result.getOutput() : null;
        ChatResponseMetadata metadata = response.getMetadata();
        Usage usage = metadata.getUsage();

        return new AiResponseDetails(
                output != null ? output.getText() : null,
                metadata.getModel(),
                metadata.getId(),
                result != null ? result.getMetadata().getFinishReason() : null,
                result != null ? result.getMetadata().getContentFilters() : Set.of(),
                response.hasToolCalls(),
                usage != null ? usage.getPromptTokens() : null,
                usage != null ? usage.getCompletionTokens() : null,
                usage != null ? usage.getTotalTokens() : null
        );
    }

    public record JsonChatRequest(String message) {
    }

    public record JsonChatResponse(String response) {
    }

    public record AiResponseDetails(
            String response,
            String model,
            String id,
            String finishReason,
            Set<String> contentFilters,
            boolean hasToolCalls,
            Integer promptTokens,
            Integer completionTokens,
            Integer totalTokens) {
    }
}
