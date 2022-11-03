package org.example.courseinfo.cli.service;

import org.example.courseinfo.domain.Course;
import org.example.courseinfo.repository.CourseRepository;
import org.example.functional.LazyList;
import org.example.functional.Result;
import org.example.functional.Unit;

public class CourseStorageService {
    private static final String PS_BASE_URL = "https://app.pluralsight.com";
    private final CourseRepository repository;

    public CourseStorageService(CourseRepository repository) {
        this.repository = repository;
    }

    public Result<Unit> store(LazyList<PluralsightCourseApi> psCourses) {
        return psCourses.foldLeft(
            Result.pure(Unit.get),
            result -> course ->
                result.flatMap(unit ->
                    repository.save(
                        new Course(
                            course.id(),
                            course.title(),
                            course.durationInMinutes(),
                            PS_BASE_URL + course.contentUrl()
                        )
                    )
                )
        );
    }
}
