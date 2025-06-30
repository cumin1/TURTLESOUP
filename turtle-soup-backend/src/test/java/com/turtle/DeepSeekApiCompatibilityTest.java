package com.turtle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DeepSeekApiCompatibilityTest {

    @Value("${spring.ai.openai.base-url}")
    private String apiBaseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Test
    public void testChatCompletion() {
        RestTemplate restTemplate = new RestTemplate();
        String url = apiBaseUrl + "/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat"); // 根据实际模型名调整
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", "你好，介绍一下你自己"));
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 100);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println("DeepSeek API 返回: " + response.getBody());
    }
} 