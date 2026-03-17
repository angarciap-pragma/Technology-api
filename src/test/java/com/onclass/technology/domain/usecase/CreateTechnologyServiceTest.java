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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Prueba reglas de negocio del caso de uso de tecnologias.
@ExtendWith(MockitoExtension.class) // Inicializa automaticamente los mocks de Mockito en cada test.
class CreateTechnologyServiceTest { // Agrupa pruebas unitarias del servicio de creacion.

    // Define el mock del puerto de persistencia.
    @Mock // Crea una implementacion simulada del repositorio.
    private TechnologyRepositoryPort technologyRepositoryPort; // Dependencia externa que el servicio usa para persistir.

    // Define la instancia del caso de uso a probar.
    private CreateTechnologyService createTechnologyService; // Objeto real que se va a ejercitar en las pruebas.

    // Inicializa el caso de uso antes de cada test.
    @BeforeEach // Indica que este metodo se ejecuta antes de cada prueba.
    void setUp() { // Prepara el escenario comun a todas las pruebas.
        // Crea el caso de uso con dependencia mockeada.
        createTechnologyService = new CreateTechnologyService(technologyRepositoryPort); // Inyecta el mock en el servicio real.
    }

    // Verifica que se registre una tecnologia valida.
    @Test
    void createTechnologyShouldSaveWhenNameIsAvailable() { // Verifica el flujo exitoso de creacion.
        // Crea el modelo de entrada con espacios para validar trim.
        Technology request = Technology.create("  Java  ", "  Backend language  "); // Simula la tecnologia que llega desde la capa superior.
        // Define que el nombre no existe en persistencia.
        when(technologyRepositoryPort.existsByNormalizedName(eq("java"))).thenReturn(Mono.just(false)); // El repositorio responde que no hay duplicado.
        // Define respuesta de guardado simulado.
        when(technologyRepositoryPort.save(any(Technology.class)))
                .thenReturn(Mono.just(Technology.rehydrate(1L, "Java", "Backend language"))); // El repositorio devuelve la entidad ya persistida.

        // Ejecuta el caso de uso y valida el resultado esperado.
        StepVerifier.create(createTechnologyService.createTechnology(request))
                .expectNextMatches(saved ->
                        saved.getId().equals(1L)
                                && saved.getName().equals("Java")
                                && saved.getDescription().equals("Backend language")) // Valida el objeto emitido por el Mono.
                .verifyComplete(); // Comprueba que el flujo termina correctamente sin errores.

        // Verifica que se consulto existencia por nombre normalizado.
        verify(technologyRepositoryPort).existsByNormalizedName("java"); // Asegura que la regla de unicidad si se consulto.
        // Verifica que se ejecuto el guardado.
        verify(technologyRepositoryPort).save(any(Technology.class)); // Asegura que el servicio intento persistir la tecnologia.
    }

    // Verifica que no permita registrar nombres repetidos.
    @Test
    void createTechnologyShouldFailWhenNameAlreadyExists() { // Verifica el flujo cuando el nombre ya existe.
        // Crea el modelo de entrada.
        Technology request = Technology.create("Node.js", "JavaScript runtime"); // Simula una tecnologia valida.
        // Define que el nombre ya existe.
        when(technologyRepositoryPort.existsByNormalizedName(eq("node.js"))).thenReturn(Mono.just(true)); // El repositorio responde que si hay duplicado.
        // Ejecuta el caso de uso y valida el error esperado.
        StepVerifier.create(createTechnologyService.createTechnology(request))
                .expectErrorMatches(error ->
                        error instanceof ConflictException
                                && error.getMessage().equals("Technology name already exists")) // Valida el tipo y mensaje del error emitido.
                .verify(); // Comprueba que el flujo falla como se esperaba.
    }

    // Verifica que no se permita payload nulo.
    @Test
    void createTechnologyShouldFailWhenPayloadIsNull() { // Verifica el comportamiento actual ante entrada nula.
        StepVerifier.create(createTechnologyService.createTechnology(null))
                .expectErrorMatches(error ->
                        error instanceof ValidationException
                                && error.getMessage().equals("Technology payload is required"))
                .verify();
    }

    // Verifica el comportamiento actual: el servicio permite guardar aun si llega id.
    @Test
    void createTechnologyShouldSaveWhenIdIsProvided() { // Verifica el comportamiento actual si el objeto ya trae id.
        Technology request = Technology.rehydrate(10L, "Java", "Backend language"); // Simula una entidad reconstruida con id.
        when(technologyRepositoryPort.existsByNormalizedName(eq("java"))).thenReturn(Mono.just(false)); // El repositorio responde que no hay duplicado.
        when(technologyRepositoryPort.save(any(Technology.class)))
                .thenReturn(Mono.just(Technology.rehydrate(10L, "Java", "Backend language"))); // El repositorio devuelve la misma entidad guardada.

        StepVerifier.create(createTechnologyService.createTechnology(request))
                .expectNextMatches(saved ->
                        saved.getId().equals(10L)
                                && saved.getName().equals("Java")
                                && saved.getDescription().equals("Backend language")) // Valida el contenido del resultado.
                .verifyComplete(); // Comprueba que el flujo termina correctamente.
    }
}
