package org.example.courseinfo.cli.service;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NetHttpClient implements ResultHttpClient {

    private final HttpClient client;

    public NetHttpClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public Mono<HttpResponse<String>> send(HttpRequest request) {
        return Mono.defer(() -> {
            try {
                // blocking call here makes the non-blocking IO useless :(
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return Mono.just(response);
            } catch (IOException | InterruptedException e) {
                return Mono.error(new RuntimeException("Http request failed", e));
            }
        });
    }
}
