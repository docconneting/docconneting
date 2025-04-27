package com.example.docconneting.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "portone.api")
public class PortOneProperties {
    private String key;
    private String secret;

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
