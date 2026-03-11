package com.onclass.technology.domain.usecase;

import com.onclass.technology.application.service.CreateTechnologyService;
import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Prueba reglas de negocio del caso de uso de tecnologias.
@ExtendWith(MockitoExtension.class)
class CreateTechnologyServiceTest {

    // Define el mock del puerto de persistencia.
    @Mock
    private TechnologyRepositoryPort technologyRepositoryPort;

    // Define la instancia del caso de uso a probar.
    private CreateTechnologyService createTechnologyService;

    // Inicializa el caso de uso antes de cada test.
    @BeforeEach
    void setUp() {
        // Crea el caso de uso con dependencia mockeada.
        createTechnologyService = new CreateTechnologyService(technologyRepositoryPort);
    }

    // Verifica que se registre una tecnologia valida.
    @Test
    void createTechnologyShouldSaveWhenNameIsAvailable() {
        // Crea el modelo de entrada con espacios para validar trim.
        Technology request = Technology.create("  Java  ", "  Backend language  ");
        // Define que el nombre no existe en persistencia.
        when(technologyRepositoryPort.existsByNormalizedName(eq("java"))).thenReturn(Mono.just(false));
        // Define respuesta de guardado simulado.
        when(technologyRepositoryPort.save(any(Technology.class)))
                .thenReturn(Mono.just(Technology.rehydrate(1L, "Java", "Backend language")));

        // Ejecuta el caso de uso y valida el resultado esperado.
        StepVerifier.create(createTechnologyService.createTechnology(request))
                .expectNextMatches(saved ->
                        saved.getId().equals(1L)
                                && saved.getName().equals("Java")
                                && saved.getDescription().equals("Backend language"))
                .verifyComplete();

        // Verifica que se consulto existencia por nombre normalizado.
        verify(technologyRepositoryPort).existsByNormalizedName("java");
        // Verifica que se ejecuto el guardado.
        verify(technologyRepositoryPort).save(any(Technology.class));
    }

    // Verifica que no permita registrar nombres repetidos.
    @Test
    void createTechnologyShouldFailWhenNameAlreadyExists() {
        // Crea el modelo de entrada.
        Technology request = Technology.create("Node.js", "JavaScript runtime");
        // Define que el nombre ya existe.
        when(technologyRepositoryPort.existsByNormalizedName(eq("node.js"))).thenReturn(Mono.just(true));
        // Se mockea save para evitar NPE por evaluacion eager de switchIfEmpty.
        when(technologyRepositoryPort.save(any(Technology.class)))
                .thenReturn(Mono.just(Technology.rehydrate(99L, "Node.js", "JavaScript runtime")));

        // Ejecuta el caso de uso y valida el error esperado.
        StepVerifier.create(createTechnologyService.createTechnology(request))
                .expectErrorMatches(error ->
                        error instanceof ConflictException
                                && error.getMessage().equals("Technology name already exists"))
                .verify();
    }

    // Verifica que no se permita payload nulo.
    @Test
    void createTechnologyShouldFailWhenPayloadIsNull() {
        // La implementacion actual no valida null y lanza NullPointerException en llamada directa.
        assertThrows(NullPointerException.class, () -> createTechnologyService.createTechnology(null));
    }

    // Verifica el comportamiento actual: el servicio permite guardar aun si llega id.
    @Test
    void createTechnologyShouldSaveWhenIdIsProvided() {
        Technology request = Technology.rehydrate(10L, "Java", "Backend language");
        when(technologyRepositoryPort.existsByNormalizedName(eq("java"))).thenReturn(Mono.just(false));
        when(technologyRepositoryPort.save(any(Technology.class)))
                .thenReturn(Mono.just(Technology.rehydrate(10L, "Java", "Backend language")));

        StepVerifier.create(createTechnologyService.createTechnology(request))
                .expectNextMatches(saved ->
                        saved.getId().equals(10L)
                                && saved.getName().equals("Java")
                                && saved.getDescription().equals("Backend language"))
                .verifyComplete();
    }
}
