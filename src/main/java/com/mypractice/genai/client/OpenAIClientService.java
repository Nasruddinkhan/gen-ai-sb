package com.mypractice.genai.client;

import com.mypractice.genai.exception.OpenAIChatException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIClientService {
    private static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_MODEL = "gpt-4o";
    private static final String CONTENT_TYPE = "application/json";
    private static final String SYSTEM_PROMPT = """
            You are helpful assistance that summerize any given content.
            Ensure the summary concise informative & capture the key points.
            User friendly & maintain the professionalism.
            Don't answer any other than the summerization. If question is not about summerization, response with 'I can 
            not summerize because it's not match with system prompt.
            """;

    private final ObjectMapper objectMapper;

    public OpenAIClientService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String chat(String prompt) throws OpenAIChatException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            var request = getRequest(prompt);
            var response = httpClient.execute(request, resp -> EntityUtils.toString(resp.getEntity()));
            return parseResponse(response);
        } catch (IOException e) {
            throw new OpenAIChatException("Couldn't call open ai via java client " + e.getMessage());
        }


    }

    private String parseResponse(String response) {
       Map<String, Object> openAIRes =  objectMapper.readValue(response, Map.class);
       return openAIRes.get("choices").toString();
    }

    private HttpPost getRequest(String prompt) {

        var request = new HttpPost(OPENAI_API_URL);
        var openAIApiKey = System.getenv(OPENAI_API_KEY);
        request.addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+openAIApiKey);
        Map<String, Object> userPrompt = Map.of(
                "role", "user",
                "content", prompt
        );
        Map<String, Object> systemPrompt = Map.of(
                "role", "system",
                "content", SYSTEM_PROMPT
        );
        var body =  Map.of(
                "model", OPENAI_MODEL,
                "messages", List.of(userPrompt, systemPrompt)
        );
        String requestBody = objectMapper.writeValueAsString(body);
        request.setEntity(new StringEntity(requestBody));
         return request;
    }
}
