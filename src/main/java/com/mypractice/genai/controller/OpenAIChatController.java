package com.mypractice.genai.controller;

import com.mypractice.genai.client.OpenAIClientService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/chat")
public class OpenAIChatController{

    private final ChatClient chatClient;
    private final OpenAIClientService openAIClientService;

    public OpenAIChatController(@Qualifier("openAIChatClient") ChatClient chatClient, OpenAIClientService openAIClientService) {
        this.chatClient = chatClient;
        this.openAIClientService = openAIClientService;
    }

    @PostMapping("/summarize")
    public String summarize(@RequestBody String msg){
        return chatClient.prompt().user(msg).call().content();
    }

    @PostMapping("/summarize-with-openai-with-http-client")
    public String summarizeWithHttpClient(@RequestBody String msg){
        return openAIClientService.chat(msg);
    }
}
