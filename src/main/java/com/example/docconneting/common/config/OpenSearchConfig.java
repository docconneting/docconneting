//package com.example.docconneting.common.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
//import org.apache.http.impl.nio.reactor.IOReactorConfig;
//import org.opensearch.client.RestClient;
//import org.opensearch.client.RestClientBuilder;
//import org.opensearch.client.json.jackson.JacksonJsonpMapper;
//import org.opensearch.client.opensearch.OpenSearchClient;
//import org.opensearch.client.transport.rest_client.RestClientTransport;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenSearchConfig {
//
//    @Value("${opensearch.username}")
//    private String username;
//
//    @Value("${opensearch.password}")
//    private String password;
//
//    @Value("${opensearch.host}")
//    private String host;
//
//    @Value("${opensearch.port}")
//    private int port;
//
//    @Bean
//    public RestClient restClient() {
//        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(
//                AuthScope.ANY,
//                new UsernamePasswordCredentials(username, password)
//        );
//
//        return RestClient.builder(new HttpHost(host, port, "https"))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                        .setDefaultCredentialsProvider(credentialsProvider)
//                        .setDefaultIOReactorConfig(IOReactorConfig.custom()
//                                .setIoThreadCount(1)
//                                .build()))
//                .build();
//    }
//
//    @Bean
//    public OpenSearchClient openSearchClient(RestClient restClient) {
//        return new OpenSearchClient(
//                new RestClientTransport(restClient, new JacksonJsonpMapper(new ObjectMapper()))
//        );
//    }
//}
