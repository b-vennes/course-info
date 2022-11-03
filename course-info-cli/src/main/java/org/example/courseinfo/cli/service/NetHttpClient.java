package org.example.courseinfo.cli.service;

import org.example.functional.Result;

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
    public Result<HttpResponse<String>> send(HttpRequest request) {
        try {
            return Result.success(client.send(request, HttpResponse.BodyHandlers.ofString()));
        } catch (IOException | InterruptedException e) {
            return Result.failure(new RuntimeException("Http request failed", e));
        }
    }
}
