package org.example.courseinfo.cli.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

public record CourseRetrievalService(ResultHttpClient client, ObjectMapper objectMapper) {

    private static final String PS_URI = "https://app.pluralsight.com/profile/data/author/%s/all-content";

    public Flux<PluralsightCourseApi> getCoursesFor(String author) {
        return client
            .send(
                HttpRequest.newBuilder()
                    .uri(URI.create(String.format(PS_URI, author)))
                    .GET()
                    .build()
            )
            .onErrorMap(error -> new RuntimeException("Failed to retrieve courses from the Pluralsight API.", error))
            .flux()
            .flatMap(response ->
                response.statusCode() == 200 ?
                    parseResponse(response.body()) :
                    Flux.error(
                        new RuntimeException("Pluralsight API call failed with status code: " + response.statusCode())
                    )
            );
    }

    private Flux<PluralsightCourseApi> parseResponse(String json) {
        return Flux.just(json)
            .flatMap(body -> {
                try {
                    List<PluralsightCourseApi> courses = objectMapper.readValue(
                        body,
                        objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, PluralsightCourseApi.class)
                    );

                    return Flux.fromIterable(courses);
                } catch (IOException e) {
                    return Flux.error(new RuntimeException("Failed to deserialize response from Pluralsight", e));
                }
            });
    }
}
