package org.example.courseinfo.cli.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PluralsightCourseTest {
    @ParameterizedTest
    @CsvSource(textBlock = """
        01:07:54.88, 67
        00:00:00.00, 0
        00:00:01.00, 0
        00:01:00, 1
        00:08:33, 8
        """)
    void durationInMinutes(String input, long expected) {
        PluralsightCourseApi course = new PluralsightCourseApi("id", "title", input, "contentUrl", false);
        assertEquals(expected, course.durationInMinutes());
    }
}
