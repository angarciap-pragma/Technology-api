package com.onclass.technology.domain.model;

import com.onclass.technology.domain.exception.ValidationException;
import lombok.Getter;
import lombok.ToString;

import java.util.Locale;

@Getter
@ToString
public class Technology {

    private static final int NAME_MAX_LENGTH = 50;
    private static final int DESCRIPTION_MAX_LENGTH = 90;

    private final Long id;
    private final String name;
    private final String description;

    private Technology(Long id, String name, String description) {
        this.id = id;
        this.name = validate(name, "name", NAME_MAX_LENGTH);
        this.description = validate(description, "description", DESCRIPTION_MAX_LENGTH);
    }

    /**
     * Factory method to create a new technology.
     */
    public static Technology create(String name, String description) {
        return new Technology(null, name, description);
    }

    /**
     * Factory method to reconstruct a technology from persistence.
     */
    public static Technology rehydrate(Long id, String name, String description) {
        if (id == null) {
            throw new ValidationException("id is required");
        }
        return new Technology(id, name, description);
    }

    /**
     * Generic validation used by domain invariants.
     */
    private static String validate(String value, String field, int maxLength) {

        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " is required");
        }

        String sanitized = value.trim();

        if (sanitized.length() > maxLength) {
            throw new ValidationException(field + " exceeds " + maxLength + " characters");
        }

        return sanitized;
    }

    /**
     * Returns a normalized name used for uniqueness checks.
     */
    public String normalizedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}