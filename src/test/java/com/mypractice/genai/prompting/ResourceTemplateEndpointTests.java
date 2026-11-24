package com.mypractice.genai.prompting;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResourceTemplateEndpointTests {

    @Test
    void postsRequestValuesAsRenderedResourcePromptAndReturnsModelAnswer() throws Exception {
        ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        String expectedPrompt = """
                Explain Dependency injection to a Java beginner.
                Use a friendly tone.
                Keep the answer under five bullet points.
                """;
        when(chatClient.prompt().user(expectedPrompt).call().content())
                .thenReturn("Spring supplies the dependencies your class needs.");

        var controller = new PromptingController(chatClient,
                new ClassPathResource("prompts/explanation.st"));
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/v1/api/prompting/step-6-resource-template-prompt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"topic":"Dependency injection","audience":"Java beginner","tone":"friendly"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.step").value("Step 6"))
                .andExpect(jsonPath("$.concept").value("Resource prompt template"))
                .andExpect(jsonPath("$.response")
                        .value("Spring supplies the dependencies your class needs."));
    }
}
