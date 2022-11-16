package org.example.courseinfo.repository;

import org.example.courseinfo.domain.Course;
import org.example.courseinfo.types.Unit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseRepository {
    Mono<Unit> save(Course course);

    Flux<Course> getAll();

    Mono<Unit> addNotes(String id, String notes);

    static CourseRepository openCourseRepository(String databaseFile) {
        return new CourseJdbcRepository(databaseFile);
    }
}
