package org.example.courseinfo.cli.service;

import org.example.courseinfo.domain.Course;
import org.example.courseinfo.repository.CourseRepository;
import org.example.functional.LazyList;
import org.example.functional.Result;
import org.example.functional.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseStorageServiceTest {

    @Test
    void store() throws Exception {
        CourseRepository repository = new InMemoryCourseRepository();
        CourseStorageService courseStorageService = new CourseStorageService(repository);

        PluralsightCourseApi ps1 =
            new PluralsightCourseApi("1", "Title 1", "01:40:00.123", "/url1", false);
        courseStorageService.store(LazyList.one(ps1));

        Course expected = new Course("1", "Title 1", 100, "https://app.pluralsight.com/url1");
        assertEquals(expected, repository.getAll().getOrThrow().head());
    }

    static class InMemoryCourseRepository implements CourseRepository {

        private LazyList<Course> courses = LazyList.empty();

        @Override
        public Result<Unit> save(Course course) {
            courses = courses.join(LazyList.one(course));
            return Result.success(Unit.get);
        }

        @Override
        public Result<LazyList<Course>> getAll() {
            return Result.success(courses);
        }
    }
}