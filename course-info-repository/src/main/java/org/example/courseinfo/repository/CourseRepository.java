package org.example.courseinfo.repository;

import org.example.courseinfo.domain.Course;
import org.example.functional.*;

import java.util.List;

public interface CourseRepository {
    Result<Unit> save(Course course);

    Result<LazyList<Course>> getAll();

    static CourseRepository openCourseRepository(String databaseFile) {
        return new CourseJdbcRepository(databaseFile);
    }
}
