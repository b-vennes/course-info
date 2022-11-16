package org.example.courseinfo.server;

import jakarta.ws.rs.*;
import org.example.courseinfo.domain.Course;
import org.example.courseinfo.repository.CourseRepository;
import org.example.courseinfo.repository.RepositoryException;
import org.example.courseinfo.types.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import jakarta.ws.rs.core.MediaType;

import java.util.Comparator;
import java.util.stream.Stream;

@Path("/courses")
public class CourseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseResource.class);

    private final CourseRepository courseRepository;

    public CourseResource(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Stream<Course> getCourses() {
        try {
            return Flux
                .defer(() -> {
                    LOGGER.info("Getting all courses");
                    return Flux.just(Unit.get);
                })
                .flatMap(ignore -> courseRepository.getAll())
                .sort(Comparator.comparing(Course::id))
                .toStream();
        } catch (RepositoryException e) {
            LOGGER.error("Error getting courses", e);
            throw new NotFoundException(e);
        }
    }

    @POST
    @Path("/{id}/notes")
    @Consumes(MediaType.TEXT_PLAIN)
    public Stream<Unit> addNotes(@PathParam("id") String id, String notes) {
        try {
            return Flux.defer(() -> {
                    LOGGER.info("Adding notes to course {}", id);
                    return Flux.just(Unit.get);
                })
                .flatMap(ignore -> courseRepository.addNotes(id, notes))
                .toStream();
        } catch (RepositoryException e) {
            LOGGER.error("Error adding notes to course {}", id, e);
            throw new NotFoundException(e);
        }
    }
}
