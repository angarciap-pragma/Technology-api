package com.onclass.technology.domain.model;

import com.onclass.technology.domain.exception.ValidationException;
import lombok.Getter;

@Getter
public class Technology {

    private static final int NAME_MAX_LENGTH = 50;
    private static final int DESCRIPTION_MAX_LENGTH = 90;

    private final Long id;
    private final String name;
    private final String description;

    private Technology(Long id, String name, String description) {
        this.id = id;
        this.name = validateName(name);
        this.description = validateDescription(description);
    }

    // Factory para crear una nueva tecnologia
    public static Technology create(String name, String description) {
        return new Technology(null, name, description);
    }

    // Factory para reconstruir desde base de datos
    public static Technology rehydrate(Long id, String name, String description) {
        if (id == null) {
            throw new ValidationException("Technology id is required");
        }
        return new Technology(id, name, description);
    }

    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Technology name is required");
        }

        String value = name.trim();

        if (value.length() > NAME_MAX_LENGTH) {
            throw new ValidationException("Technology name exceeds 50 characters");
        }

        return value;
    }

    private static String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Technology description is required");
        }

        String value = description.trim();

        if (value.length() > DESCRIPTION_MAX_LENGTH) {
            throw new ValidationException("Technology description exceeds 90 characters");
        }

        return value;
    }
}