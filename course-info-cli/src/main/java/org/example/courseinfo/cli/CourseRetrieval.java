package org.example.courseinfo.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.courseinfo.cli.service.CourseRetrievalService;
import org.example.courseinfo.cli.service.CourseStorageService;
import org.example.courseinfo.cli.service.NetHttpClient;
import org.example.courseinfo.cli.service.PluralsightCourseApi;
import org.example.courseinfo.repository.CourseRepository;
import org.example.functional.LazyList;
import org.example.functional.Result;
import org.example.functional.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;

import static java.util.function.Predicate.not;

public class CourseRetrieval {

    private static final Logger LOG = LoggerFactory.getLogger(CourseRetrieval.class);

    public static void main(String[] args) {
        LOG.info("Course retrieval started.");

        Configuration.parse(args)
            .adaptError(error -> new RuntimeException("Failed to parse configuration.", error))
            .flatMap(CourseRetrieval::retrieveCourses)
            .logError("A failure has occurred while running the course retrieval program.", LOG);
    }

    private static Result<Unit> retrieveCourses(Configuration config) {
        LOG.info("Retrieving courses for author '{}'.", config.author());

        CourseRetrievalService courseRetrievalService =
            new CourseRetrievalService(
                new NetHttpClient(
                    HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build()
                ),
                new ObjectMapper()
            );

        CourseStorageService courseStorageService =
            new CourseStorageService(CourseRepository.openCourseRepository("./courses.db"));

        return courseRetrievalService
            .getCoursesFor(config.author())
            .map(courses -> courses.filter(not(PluralsightCourseApi::isRetired)))
            .log(r -> "Retrieved the following " + r.length() + " courses " + r, LOG)
            .flatMap(courseStorageService::store)
            .log(unit -> "Courses successfully stored.", LOG);
    }
}
