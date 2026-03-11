package com.onclass.technology.domain.model;

import com.onclass.technology.domain.exception.ValidationException; // Importa la excepcion esperada en validaciones.
import org.junit.jupiter.api.Test; // Importa la anotacion que marca un metodo como prueba.

import static org.junit.jupiter.api.Assertions.assertEquals; // Permite comparar el valor esperado con el real.
import static org.junit.jupiter.api.Assertions.assertThrows; // Permite verificar que una accion lance una excepcion.

// Prueba invariantes del modelo de dominio Technology.
class TechnologyTest {

    @Test
    void createShouldKeepOriginalValues() { // Verifica que el objeto conserve los valores originales al crearse.
        Technology technology = Technology.create("  Java  ", "  Backend language  "); // Crea una tecnologia con espacios intencionales.

        assertEquals("  Java  ", technology.getName()); // Comprueba que el nombre guardado sea exactamente el recibido.
        assertEquals("  Backend language  ", technology.getDescription()); // Comprueba que la descripcion guardada sea exactamente la recibida.
        assertEquals(null, technology.getId()); // Comprueba que el id inicial sea nulo porque aun no se ha persistido.
    }

    @Test
    void normalizedNameShouldTrimCollapseSpacesAndLowercase() { // Verifica la normalizacion del nombre.
        Technology technology = Technology.create("  JaVa   Script   ", "Runtime"); // Crea una tecnologia con mayusculas y espacios extra.

        assertEquals("java script", technology.normalizedName()); // Espera un nombre sin espacios sobrantes y en minuscula.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void createShouldFailWhenNameIsBlank() { // Verifica que no se permita un nombre vacio o en blanco.
        ValidationException exception = assertThrows( // Guarda la excepcion producida por la accion.
                ValidationException.class, // Define el tipo de excepcion esperado.
                () -> Technology.create("   ", "Valid description") // Ejecuta la creacion con un nombre invalido.
        ); // Cierra la verificacion de excepcion.

        assertEquals("name is required", exception.getMessage()); // Comprueba el mensaje exacto del error.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void createShouldFailWhenDescriptionIsBlank() { // Verifica que no se permita una descripcion vacia o en blanco.
        ValidationException exception = assertThrows( // Guarda la excepcion producida por la accion.
                ValidationException.class, // Define el tipo de excepcion esperado.
                () -> Technology.create("Java", "   ") // Ejecuta la creacion con descripcion invalida.
        ); // Cierra la verificacion de excepcion.

        assertEquals("description is required", exception.getMessage()); // Comprueba el mensaje exacto del error.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void createShouldFailWhenNameLengthExceedsLimit() { // Verifica que el nombre no supere el maximo permitido.
        String invalidName = "A".repeat(51); // Construye un nombre de 51 caracteres.

        ValidationException exception = assertThrows( // Guarda la excepcion producida por la accion.
                ValidationException.class, // Define el tipo de excepcion esperado.
                () -> Technology.create(invalidName, "Valid description") // Ejecuta la creacion con un nombre demasiado largo.
        ); // Cierra la verificacion de excepcion.

        assertEquals("name exceeds 50 characters", exception.getMessage()); // Comprueba el mensaje exacto del error.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void createShouldFailWhenDescriptionLengthExceedsLimit() { // Verifica que la descripcion no supere el maximo permitido.
        String invalidDescription = "D".repeat(91); // Construye una descripcion de 91 caracteres.

        ValidationException exception = assertThrows( // Guarda la excepcion producida por la accion.
                ValidationException.class, // Define el tipo de excepcion esperado.
                () -> Technology.create("Java", invalidDescription) // Ejecuta la creacion con una descripcion demasiado larga.
        ); // Cierra la verificacion de excepcion.

        assertEquals("description exceeds 90 characters", exception.getMessage()); // Comprueba el mensaje exacto del error.
    }
} // Fin de la clase de pruebas del modelo Technology.
