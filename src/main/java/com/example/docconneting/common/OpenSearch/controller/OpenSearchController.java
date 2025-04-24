package com.example.docconneting.common.OpenSearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;

@RestController
@RequiredArgsConstructor
public class OpenSearchController {

    private final RestClient restClient;

    //연결 확인용
    @GetMapping("/opensearch/health")
    public String getClusterHealth() throws IOException {
        Request request = new Request("GET", "/_cluster/health");
        Response response = restClient.performRequest(request);
        return response.getStatusLine().toString(); // ex: HTTP/1.1 200 OK
    }
}
