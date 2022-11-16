package org.example.courseinfo.cli.service;

import reactor.core.publisher.Mono;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface ResultHttpClient {
    Mono<HttpResponse<String>> send(HttpRequest request);
}
