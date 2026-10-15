package com.mypractice.genai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class DefaultSystemPromptAdvisor implements CallAdvisor, StreamAdvisor {

    private static final String NAME = "DefaultSystemPromptAdvisor";

    private final String systemPrompt;
    private final int order;

    public DefaultSystemPromptAdvisor(String systemPrompt) {
        this(systemPrompt, Ordered.HIGHEST_PRECEDENCE + 10);
    }

    public DefaultSystemPromptAdvisor(String systemPrompt, int order) {
        this.systemPrompt = systemPrompt;
        this.order = order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        return chain.nextCall(addSystemPrompt(request));
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        return chain.nextStream(addSystemPrompt(request));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return order;
    }

    private ChatClientRequest addSystemPrompt(ChatClientRequest request) {
        Prompt prompt = request.prompt();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.addAll(prompt.getInstructions());

        Prompt updatedPrompt = Prompt.builder()
                .messages(messages)
                .chatOptions(prompt.getOptions())
                .build();

        return request.mutate()
                .prompt(updatedPrompt)
                .context(request.context())
                .context("systemPromptAdvisor", getName())
                .build();
    }
}
