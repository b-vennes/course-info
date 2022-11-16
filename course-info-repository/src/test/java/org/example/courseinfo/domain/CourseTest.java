package org.example.courseinfo.domain;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {
    @Test
    void rejectNullComponents() {
        assertThrows(IllegalArgumentException.class, () -> new Course(null, null, 1, null, Optional.empty()));
    }

    @Test
    void rejectBlankNotes() {
        assertThrows(IllegalArgumentException.class, () -> new Course("id", "name", 1, "url", Optional.of("")));
    }
}