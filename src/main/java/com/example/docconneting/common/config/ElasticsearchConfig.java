package com.example.docconneting.common.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;


@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private final Environment env;

    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.port}")
    private String port;

    @Value("${elastic.username:}")  // 빈 값 허용
    private String username;

    @Value("${elastic.password:}")  // 빈 값 허용
    private String password;

    public ElasticsearchConfig(Environment env) {
        this.env = env;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        var builder = ClientConfiguration.builder()
                .connectedTo(host + ":" + port)
                .usingSsl(false);

        if (env.acceptsProfiles("local")) {
            builder.withBasicAuth(username, password);
        }

        return builder.build();
    }

}
