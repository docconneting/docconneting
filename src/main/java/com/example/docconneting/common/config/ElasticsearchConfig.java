package com.example.docconneting.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    // 로컬/배포 분기 나눔
    @Value("${spring.data.elasticsearch.uris}")
    private String elasticUri;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticUri.replace("http://", ""))

                .build();
    }
}