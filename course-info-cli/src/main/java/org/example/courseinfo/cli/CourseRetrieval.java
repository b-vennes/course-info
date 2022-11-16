package org.example.courseinfo.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.courseinfo.cli.service.CourseRetrievalService;
import org.example.courseinfo.cli.service.CourseStorageService;
import org.example.courseinfo.cli.service.NetHttpClient;
import org.example.courseinfo.cli.service.PluralsightCourseApi;
import org.example.courseinfo.repository.CourseRepository;
import org.example.courseinfo.types.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;

import static java.util.function.Predicate.not;

public class CourseRetrieval {

    private static final Logger LOG = LoggerFactory.getLogger(CourseRetrieval.class);

    public static void main(String[] args) {
        LOG.info("Course retrieval started.");

        Configuration.parse(args)
            .onErrorMap(error -> new RuntimeException("Failed to parse configuration.", error))
            .flatMap(CourseRetrieval::retrieveCourses)
            .doOnError(error -> LOG.error("A failure has occurred while running the course retrieval program."))
            .block();
    }

    private static Mono<Unit> retrieveCourses(Configuration config) {
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
            .filter(not(PluralsightCourseApi::isRetired))
            .transform(coursesStream ->
                coursesStream
                    .collectList()
                    .flatMapMany(courses -> {
                        LOG.info("Retrieved {} courses.", courses.size());
                        return coursesStream;
                    })
            )
            .transform(courseStorageService::store)
            .collect(Unit.collector)
            .map(unit -> {
                LOG.info("Course retrieval completed successfully.");
                return unit;
            });
    }
}
