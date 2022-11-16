package org.example.courseinfo.server;

import org.example.courseinfo.repository.CourseRepository;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.logging.LogManager;

public class CourseServer {

    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    private static final Logger LOG = LoggerFactory.getLogger(CourseServer.class);

    public static void main(String[] args) {
        LOG.info("Starting course server.");
        Configuration serverConfig = loadConfiguration();
        CourseRepository courseRepository = CourseRepository.openCourseRepository(serverConfig.databaseFileName());
        ResourceConfig config = new ResourceConfig().register(new CourseResource(courseRepository));
        GrizzlyHttpServerFactory.createHttpServer(URI.create(serverConfig.baseUrl()), config);
    }

    private static Configuration loadConfiguration() {
        try (InputStream propertiesStream = CourseServer.class.getResourceAsStream("/server.properties")) {
            Properties properties = new Properties();
            properties.load(propertiesStream);

            String baseUrl = properties.getProperty("course-info.base-url");
            String databaseFilename = properties.getProperty("course-info.database");

            return new Configuration(baseUrl, databaseFilename);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load database filename.", e);
        }
    }
}
