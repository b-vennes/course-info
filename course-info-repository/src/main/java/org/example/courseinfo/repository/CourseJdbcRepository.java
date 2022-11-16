package org.example.courseinfo.repository;

import org.example.courseinfo.domain.Course;
import org.example.courseinfo.types.Unit;
import org.h2.jdbcx.JdbcDataSource;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

class CourseJdbcRepository implements CourseRepository {

    private static final String H2_DATABASE_URL = "jdbc:h2:file:%s;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM './db_init.sql'";
    private static final String INSERT_COURSE = """
        MERGE INTO Courses (id, name, length, url)
        VALUES (?, ?, ?, ?)
        """;

    private static final String INSERT_NOTES = """
        UPDATE Courses SET notes = ? WHERE id = ?
        """;

    private final DataSource dataSource;

    public CourseJdbcRepository(String databaseFile) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl(H2_DATABASE_URL.formatted(databaseFile));
        this.dataSource = jdbcDataSource;
    }

    @Override
    public Mono<Unit> save(Course course) {
        return Mono.defer(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(INSERT_COURSE);
                statement.setString(1, course.id());
                statement.setString(2, course.name());
                statement.setLong(3, course.length());
                statement.setString(4, course.url());
                statement.executeUpdate();
                return Mono.just(Unit.get);
            } catch (SQLException e) {
                return Mono.error(e);
            }
        });
    }

    @Override
    public Flux<Course> getAll() {
        return Flux.defer(() -> {
            try(Connection connection = dataSource.getConnection()) {
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM Courses");

                Flux<Course> courses = Flux.empty();

                // yuck
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    long length = resultSet.getLong("length");
                    String url = resultSet.getString("url");
                    Optional<String> notes = Optional.ofNullable(resultSet.getString("notes"));

                    courses = courses.concatWith(Mono.just(new Course(id, name, length, url, notes)));
                }

                return courses;
            } catch (SQLException e) {
                return Flux.error(new RepositoryException("Failed to get all courses", e));
            }
        });
    }

    @Override
    public Mono<Unit> addNotes(String id, String notes) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(INSERT_NOTES);
            statement.setString(1, notes);
            statement.setString(2, id);
            statement.executeUpdate();
            return Mono.just(Unit.get);
        } catch (SQLException e) {
            return Mono.error(new RepositoryException("Failed to add notes to " + id, e));
        }
    }
}
