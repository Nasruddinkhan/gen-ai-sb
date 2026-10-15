package com.mypractice.genai.config;

import com.mypractice.genai.prompting.PromptingController;
import com.mypractice.genai.prompting.PromptingTechniqueController;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "HUGGINGFACE_API_KEY=test-token",
        "HUGGINGFACE_MODEL=test-chat-model"
})
@ActiveProfiles("huggingface")
class HuggingFaceProfileTests {

    @Autowired
    ApplicationContext context;

    @Autowired
    Environment environment;

    @Test
    void wiresHuggingFaceThroughOpenAiClientForPrompting() {
        assertThat(environment.getProperty("spring.ai.openai.base-url"))
                .isEqualTo("https://router.huggingface.co/v1");
        assertThat(environment.getProperty("spring.ai.openai.api-key")).isEqualTo("test-token");
        assertThat(environment.getProperty("spring.ai.openai.chat.options.model")).isEqualTo("test-chat-model");
        assertThat(context.getBeansOfType(OpenAiChatModel.class)).hasSize(1);
        assertThat(context.getBeansOfType(OllamaChatModel.class)).isEmpty();
        assertThat(context.getBean("chatClient", ChatClient.class))
                .isSameAs(context.getBean("openAIChatClient"));
        assertThat(context.getBean(PromptingController.class)).isNotNull();
        assertThat(context.getBean(PromptingTechniqueController.class)).isNotNull();
    }
}
