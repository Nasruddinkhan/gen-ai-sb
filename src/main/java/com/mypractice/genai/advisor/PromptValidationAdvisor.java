package com.mypractice.genai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.List;

public class PromptValidationAdvisor implements CallAdvisor, StreamAdvisor {

    private static final String NAME = "PromptValidationAdvisor";

    private final int maxApproximateTokens;
    private final int order;

    public PromptValidationAdvisor(int maxApproximateTokens) {
        this(maxApproximateTokens, Ordered.HIGHEST_PRECEDENCE);
    }

    public PromptValidationAdvisor(int maxApproximateTokens, int order) {
        this.maxApproximateTokens = maxApproximateTokens;
        this.order = order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String validationError = validate(request);
        if (validationError != null) {
            return validationFailure(request, validationError);
        }

        return chain.nextCall(request);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        String validationError = validate(request);
        if (validationError != null) {
            return Flux.just(validationFailure(request, validationError));
        }

        return chain.nextStream(request);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return order;
    }

    private ChatClientResponse validationFailure(ChatClientRequest request, String message) {
        ChatResponse chatResponse = ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage(message))))
                .build();

        return ChatClientResponse.builder()
                .chatResponse(chatResponse)
                .context(request.context())
                .context("validationStatus", "blocked")
                .context("validationAdvisor", getName())
                .build();
    }

    private String validate(ChatClientRequest request) {
        UserMessage userMessage = request.prompt().getUserMessage();
        String userText = userMessage != null ? userMessage.getText() : null;

        if (!StringUtils.hasText(userText)) {
            return "Prompt validation failed: message is required.";
        }

        int approximateTokens = countApproximateTokens(userText);
        if (approximateTokens > maxApproximateTokens) {
            return "Prompt validation failed: message is too long. Maximum allowed approximate tokens is "
                    + maxApproximateTokens + ", but request has " + approximateTokens + ".";
        }

        return null;
    }

    private int countApproximateTokens(String text) {
        return text.trim().split("\\s+").length;
    }
}
