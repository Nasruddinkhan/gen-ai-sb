package com.mypractice.genai.prompting;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PromptResourceTests {

    @Test
    void resourceTemplateRendersIndependentRequestValues() {
        var template = new PromptTemplate(new ClassPathResource("prompts/explanation.st"));

        String first = template.render(Map.of(
                "topic", "dependency injection", "audience", "Java beginner", "tone", "friendly"));
        String second = template.render(Map.of(
                "topic", "chat models", "audience", "Spring developer", "tone", "formal"));

        assertThat(first).contains("dependency injection", "Java beginner", "friendly")
                .doesNotContain("{topic}", "{audience}", "{tone}");
        assertThat(second).contains("chat models", "Spring developer", "formal")
                .doesNotContain("dependency injection", "Java beginner", "friendly");
    }
}
