package org.example.courseinfo.cli.service;

import org.example.courseinfo.domain.Course;
import org.example.courseinfo.repository.CourseRepository;
import org.example.courseinfo.types.Unit;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CourseStorageServiceTest {

    @Test
    void store() {
        CourseRepository repository = new InMemoryCourseRepository();
        CourseStorageService courseStorageService = new CourseStorageService(repository);

        PluralsightCourseApi ps1 =
            new PluralsightCourseApi("1", "Title 1", "01:40:00.123", "/url1", false);
        Mono<Unit> updatedStore = courseStorageService.store(Flux.just(ps1));

        Course expected = new Course("1", "Title 1", 100, "https://app.pluralsight.com/url1", Optional.empty());
        Course actual = updatedStore.flux().flatMap(unit -> repository.getAll()).blockFirst();

        assertEquals(expected, actual);
    }

    static class InMemoryCourseRepository implements CourseRepository {

        // danger: mutable flux value not thread-safe
        private Flux<Course> courses;

        public InMemoryCourseRepository() {
            courses = Flux.empty();
        }

        @Override
        public Mono<Unit> save(Course course) {
            return Mono.defer(() -> {
                courses = courses.transform(flux -> flux.concatWith(Flux.just(course)));
                return Mono.just(Unit.get);
            });
        }

        @Override
        public Flux<Course> getAll() {
            return courses;
        }

        @Override
        public Mono<Unit> addNotes(String id, String notes) {
            return null;
        }
    }
}