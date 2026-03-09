package com.onclass.technology.domain.model;

import com.onclass.technology.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Prueba invariantes del modelo de dominio Technology.
class TechnologyTest {

    @Test
    void createShouldTrimValues() {
        Technology technology = Technology.create("  Java  ", "  Backend language  ");

        assertEquals("Java", technology.getName());
        assertEquals("Backend language", technology.getDescription());
        assertEquals(null, technology.getId());
    }

    @Test
    void createShouldFailWhenNameIsBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Technology.create("   ", "Valid description")
        );

        assertEquals("Technology name is required", exception.getMessage());
    }

    @Test
    void createShouldFailWhenDescriptionIsBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Technology.create("Java", "   ")
        );

        assertEquals("Technology description is required", exception.getMessage());
    }

    @Test
    void createShouldFailWhenNameLengthExceedsLimit() {
        String invalidName = "A".repeat(51);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Technology.create(invalidName, "Valid description")
        );

        assertEquals("Technology name exceeds 50 characters", exception.getMessage());
    }

    @Test
    void createShouldFailWhenDescriptionLengthExceedsLimit() {
        String invalidDescription = "D".repeat(91);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Technology.create("Java", invalidDescription)
        );

        assertEquals("Technology description exceeds 90 characters", exception.getMessage());
    }
}
