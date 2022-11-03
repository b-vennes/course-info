package org.example.courseinfo.repository;

import org.example.courseinfo.domain.Course;
import org.example.functional.*;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CourseJdbcRepository implements CourseRepository {

    private static final String H2_DATABASE_URL = "jdbc:h2:file:%s;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM './db_init.sql'";
    private static final String INSERT_COURSE = """
        MERGE INTO Courses (id, name, length, url)
        VALUES (?, ?, ?, ?)
        """;

    private final DataSource dataSource;

    public CourseJdbcRepository(String databaseFile) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl(H2_DATABASE_URL.formatted(databaseFile));
        this.dataSource = jdbcDataSource;
    }

    @Override
    public Result<Unit> save(Course course) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(INSERT_COURSE);
            statement.setString(1, course.id());
            statement.setString(2, course.name());
            statement.setLong(3, course.length());
            statement.setString(4, course.url());
            return Result.success(statement.executeUpdate()).unit();
        } catch (SQLException e) {
            return Result.failure(new RepositoryException("Failed to save course '%s'".formatted(course), e));
        }
    }

    @Override
    public Result<LazyList<Course>> getAll() {
        try(Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            return Result.success(
                LazyList.resultSet(
                    statement.executeQuery("SELECT * FROM Courses"),
                    r -> {
                        try {
                            return Result.success(
                                new Course(
                                    r.getString("id"),
                                    r.getString("name"),
                                    r.getLong("length"),
                                    r.getString("url")
                                )
                            );
                        } catch (SQLException e) {
                            return Result.failure(e);
                        }
                    }
                )
            );
        } catch (SQLException e) {
            return Result.failure(new RepositoryException("Failed to get all courses", e));
        }
    }
}
