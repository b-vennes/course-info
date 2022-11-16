package org.example.courseinfo.cli.service;

import org.example.courseinfo.domain.Course;
import org.example.courseinfo.repository.CourseRepository;
import org.example.courseinfo.types.Unit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class CourseStorageService {
    private static final String PS_BASE_URL = "https://app.pluralsight.com";
    private final CourseRepository repository;

    public CourseStorageService(CourseRepository repository) {
        this.repository = repository;
    }

    public Mono<Unit> store(Flux<PluralsightCourseApi> psCourses) {
        return psCourses
            .map(psCourse ->
                new Course(
                    psCourse.id(),
                    psCourse.title(),
                    psCourse.durationInMinutes(),
                    PS_BASE_URL + psCourse.contentUrl(),
                    Optional.empty()
                )
            )
            .flatMap(repository::save)
            .collect(Unit.collector);
    }
}
