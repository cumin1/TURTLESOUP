package com.turtle.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ai.openai")
@Data
public class SpringAiProperties {
    private String apiKey;
    private String baseUrl;
}
