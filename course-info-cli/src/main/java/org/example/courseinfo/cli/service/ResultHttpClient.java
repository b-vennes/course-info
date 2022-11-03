package org.example.courseinfo.cli.service;

import org.example.functional.Result;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface ResultHttpClient {
    Result<HttpResponse<String>> send(HttpRequest request);
}
