package com.mypractice.genai.prompting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class JailbreakControllerTests {

    @ParameterizedTest
    @ValueSource(strings = {"baseline", "role-play", "hypothetical", "dual-persona"})
    void sendsExampleAsUserMessageWithSeparateSystemRules(String technique) throws Exception {
        var client = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        var resource = new ClassPathResource("prompts/prompt-hacking-system.st");
        var controller = new JailbreakController(client, resource);
        var example = controller.techniques().stream()
                .filter(item -> item.id().equals(technique)).findFirst().orElseThrow();
        var userSpec = client.prompt().system(new PromptTemplate(resource).render());
        when(userSpec.user(example.attackPrompt()).call().content()).thenReturn("Model answer");
        clearInvocations(userSpec);

        MockMvcBuilders.standaloneSetup(controller).build()
                .perform(post("/v1/api/jailbreak/techniques/" + technique))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technique").value(technique))
                .andExpect(jsonPath("$.attackPrompt").value(example.attackPrompt()))
                .andExpect(jsonPath("$.response").value("Model answer"));
        verify(userSpec).user(example.attackPrompt());
    }

    @Test
    void rejectsUnknownTechniqueWithoutCallingOpenAi() throws Exception {
        var client = mock(ChatClient.class);
        var controller = new JailbreakController(client,
                new ClassPathResource("prompts/prompt-hacking-system.st"));
        MockMvcBuilders.standaloneSetup(controller).build()
                .perform(post("/v1/api/jailbreak/techniques/unknown"))
                .andExpect(status().isNotFound());
        verifyNoInteractions(client);
    }
}
