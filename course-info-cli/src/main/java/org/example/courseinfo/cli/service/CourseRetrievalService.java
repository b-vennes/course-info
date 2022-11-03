package org.example.courseinfo.cli.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.functional.LazyList;
import org.example.functional.Result;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

public record CourseRetrievalService(ResultHttpClient client, ObjectMapper objectMapper) {

    private static final String PS_URI = "https://app.pluralsight.com/profile/data/author/%s/all-content";

    public Result<LazyList<PluralsightCourseApi>> getCoursesFor(String author) {
        return client
            .send(
                HttpRequest.newBuilder()
                    .uri(URI.create(String.format(PS_URI, author)))
                    .GET()
                    .build()
            )
            .adaptError(error -> new RuntimeException("Failed to retrieve courses from the Pluralsight API.", error))
            .flatMap(response ->
                response.statusCode() == 200 ?
                    parseResponse(response.body()) :
                    Result.failure(
                        new RuntimeException("Pluralsight API call failed with status code: " + response.statusCode())
                    )
            )
            .map(LazyList::list);
    }

    private Result<List<PluralsightCourseApi>> parseResponse(String json) {
        return Result.success(json)
            .flatMap(body -> {
                try {
                    return Result.success(
                        objectMapper.readValue(
                            body,
                            objectMapper
                                .getTypeFactory()
                                .constructCollectionType(List.class, PluralsightCourseApi.class)
                        )
                    );
                } catch (IOException e) {
                    return Result.failure(new RuntimeException("Failed to deserialize response from Pluralsight", e));
                }
            });
    }
}
