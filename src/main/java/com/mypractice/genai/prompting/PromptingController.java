package com.mypractice.genai.prompting;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/prompting")
public class PromptingController {

    private final ChatClient chatClient;

    public PromptingController(@Qualifier("chatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/step-1-user-prompt")
    public PromptingResponse userPrompt(@RequestBody PromptingRequest request) {
        String response = chatClient.prompt()
                .user(request.message())
                .call()
                .content();

        return new PromptingResponse(
                "Step 1",
                "User prompt",
                "Only the user message is sent to the model.",
                response
        );
    }

    @PostMapping("/step-2-system-prompt")
    public PromptingResponse systemPrompt(@RequestBody PromptingRequest request) {
        String response = chatClient.prompt()
                .system("""
                        You are a patient Spring AI teacher.
                        Explain concepts in simple language with short examples.
                        """)
                .user(request.message())
                .call()
                .content();

        return new PromptingResponse(
                "Step 2",
                "System prompt plus user prompt",
                "The system prompt controls model behavior, and the user prompt provides the task.",
                response
        );
    }

    @PostMapping("/step-3-template-prompt")
    public PromptingResponse templatePrompt(@RequestBody TemplatePromptRequest request) {
        PromptTemplate promptTemplate = new PromptTemplate("""
                Explain {topic} to a {audience}.
                Use a {tone} tone.
                Keep the answer under five bullet points.
                """);

        String renderedPrompt = promptTemplate.render(Map.of(
                "topic", request.topic(),
                "audience", request.audience(),
                "tone", request.tone()
        ));

        String response = chatClient.prompt()
                .user(renderedPrompt)
                .call()
                .content();

        return new PromptingResponse(
                "Step 3",
                "Prompt template",
                "A template reuses the same prompt structure with different input values.",
                response
        );
    }

    @PostMapping("/step-4-few-shot-prompt")
    public PromptingResponse fewShotPrompt(@RequestBody PromptingRequest request) {
        String response = chatClient.prompt()
                .system("""
                        You rewrite technical ideas in a simple style.

                        Example 1:
                        Input: ChatClient sends prompts to AI models.
                        Output: ChatClient is the object we use to ask the AI model a question.

                        Example 2:
                        Input: Advisors can intercept requests.
                        Output: Advisors are like filters that run before or after the AI call.

                        Follow the same simple style for the user's input.
                        """)
                .user(request.message())
                .call()
                .content();

        return new PromptingResponse(
                "Step 4",
                "Few-shot prompt",
                "Few-shot prompting gives examples so the model can follow the expected style.",
                response
        );
    }

    @PostMapping("/step-5-output-format-prompt")
    public PromptingResponse outputFormatPrompt(@RequestBody PromptingRequest request) {
        String response = chatClient.prompt()
                .system("""
                        Answer using exactly this format:

                        Concept:
                        Explanation:
                        Example:
                        When to use:

                        Keep each section concise.
                        """)
                .user(request.message())
                .call()
                .content();

        return new PromptingResponse(
                "Step 5",
                "Output format prompt",
                "Output-format prompting tells the model how to structure the answer.",
                response
        );
    }

    public record PromptingRequest(String message) {
    }

    public record TemplatePromptRequest(String topic, String audience, String tone) {
    }

    public record PromptingResponse(String step, String concept, String description, String response) {
    }
}
