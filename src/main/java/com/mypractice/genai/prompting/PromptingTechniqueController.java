package com.mypractice.genai.prompting;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/prompting/techniques")
public class PromptingTechniqueController {

    private final ChatClient chatClient;

    public PromptingTechniqueController(@Qualifier("chatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/role")
    public TechniqueResponse rolePrompting(@RequestBody TechniqueRequest request) {
        String response = chatClient.prompt()
                .system("""
                        You are a Java teacher.
                        Explain the answer like you are teaching a Java developer.
                        Use simple language and one short Java-related example when useful.
                        """)
                .user(request.message())
                .call()
                .content();

        return new TechniqueResponse(
                "Role prompting",
                "Assigns the model a role so it answers from that perspective.",
                response
        );
    }

    @PostMapping("/few-shot")
    public TechniqueResponse fewShotPrompting(@RequestBody TechniqueRequest request) {
        String response = chatClient.prompt()
                .system("""
                        Convert technical text into beginner-friendly explanations.

                        Example 1:
                        Input: Dependency injection provides objects automatically.
                        Output: Dependency injection means Spring gives your class the objects it needs.

                        Example 2:
                        Input: ChatClient sends prompts to an AI model.
                        Output: ChatClient is the Spring AI object used to ask the AI model a question.

                        Follow the same pattern for the user's input.
                        """)
                .user(request.message())
                .call()
                .content();

        return new TechniqueResponse(
                "Few-shot prompting",
                "Provides examples before asking so the model follows the same style or pattern.",
                response
        );
    }

    @PostMapping("/output-format")
    public TechniqueResponse outputFormatPrompting(@RequestBody TechniqueRequest request) {
        String response = chatClient.prompt()
                .system("""
                        Return the answer using exactly this structure:

                        Summary:
                        Key points:
                        - point 1
                        - point 2
                        - point 3
                        Example:
                        """)
                .user(request.message())
                .call()
                .content();

        return new TechniqueResponse(
                "Output format prompting",
                "Forces the response into a requested structure such as JSON, bullet points, table, or sections.",
                response
        );
    }

    @PostMapping("/constraint")
    public TechniqueResponse constraintPrompting(@RequestBody TechniqueRequest request) {
        String response = chatClient.prompt()
                .system("""
                        Answer with these constraints:
                        - Keep the answer under 100 words.
                        - Use simple language.
                        - Do not include unrelated details.
                        """)
                .user(request.message())
                .call()
                .content();

        return new TechniqueResponse(
                "Constraint prompting",
                "Adds limits such as word count, tone, length, or allowed details.",
                response
        );
    }

    @PostMapping("/audience")
    public TechniqueResponse audiencePrompting(@RequestBody TechniqueRequest request) {
        String response = chatClient.prompt()
                .system("""
                        Explain the answer for a beginner.
                        Assume the reader knows basic Java but is new to Spring AI.
                        Avoid advanced terminology unless you explain it.
                        """)
                .user(request.message())
                .call()
                .content();

        return new TechniqueResponse(
                "Audience prompting",
                "Defines who the answer is for so the model can choose the right depth and language.",
                response
        );
    }

    @PostMapping("/step-by-step")
    public TechniqueResponse stepByStepPrompting(@RequestBody TechniqueRequest request) {
        String response = chatClient.prompt()
                .system("""
                        Answer step by step using this order:
                        1. Explain the concept.
                        2. Show a small example.
                        3. Explain when to use it.
                        4. Mention one common mistake.
                        """)
                .user(request.message())
                .call()
                .content();

        return new TechniqueResponse(
                "Step-by-step prompting",
                "Asks the model to reason or explain in an ordered sequence.",
                response
        );
    }

    public record TechniqueRequest(String message) {
    }

    public record TechniqueResponse(String technique, String description, String response) {
    }
}
