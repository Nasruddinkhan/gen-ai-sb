package com.mypractice.genai.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiProfileTests {

    @Test
    void defaultOpenAiBaseUrlIncludesApiVersion() throws Exception {
        var properties = new YamlPropertySourceLoader()
                .load("openai", new ClassPathResource("application-openai.yaml"));
        var environment = new StandardEnvironment();
        // Keep this check independent of a developer's environment overrides.
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        properties.forEach(environment.getPropertySources()::addLast);

        assertThat(environment.getProperty("spring.ai.openai.base-url"))
                .isEqualTo("https://api.openai.com/v1");
    }
}
