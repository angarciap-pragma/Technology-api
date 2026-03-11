package com.onclass.technology.domain.usecase; // Define el paquete donde vive esta clase de prueba.

import com.onclass.technology.application.service.DeleteTechnologyService; // Importa el servicio real que se va a probar.
import com.onclass.technology.domain.exception.NotFoundException; // Importa la excepcion esperada si el registro no existe.
import com.onclass.technology.domain.exception.ValidationException; // Importa la excepcion esperada si el id es invalido.
import com.onclass.technology.domain.port.TechnologyRepositoryPort; // Importa el puerto que sera simulado con Mockito.
import org.junit.jupiter.api.BeforeEach; // Importa la anotacion para ejecutar codigo antes de cada test.
import org.junit.jupiter.api.Test; // Importa la anotacion que marca un metodo como prueba.
import org.junit.jupiter.api.extension.ExtendWith; // Permite extender JUnit con soporte de Mockito.
import org.mockito.Mock; // Permite declarar mocks.
import org.mockito.junit.jupiter.MockitoExtension; // Activa la integracion de Mockito con JUnit 5.
import reactor.core.publisher.Mono; // Representa un flujo reactivo con cero o un elemento.
import reactor.test.StepVerifier; // Permite probar flujos reactivos paso a paso.

import static org.mockito.ArgumentMatchers.eq; // Permite exigir un argumento exacto en un mock.
import static org.mockito.Mockito.verify; // Permite verificar llamadas realizadas al mock.
import static org.mockito.Mockito.when; // Permite definir el comportamiento del mock.

@ExtendWith(MockitoExtension.class) // Inicializa automaticamente los mocks de Mockito en cada test.
class DeleteTechnologyServiceTest { // Agrupa pruebas unitarias del servicio de eliminacion.

    @Mock // Crea una implementacion simulada del repositorio.
    private TechnologyRepositoryPort technologyRepositoryPort; // Dependencia externa que el servicio usa para consultar y borrar.

    private DeleteTechnologyService deleteTechnologyService; // Objeto real que se va a ejercitar en las pruebas.

    @BeforeEach // Indica que este metodo se ejecuta antes de cada prueba.
    void setUp() { // Prepara el escenario comun a todas las pruebas.
        deleteTechnologyService = new DeleteTechnologyService(technologyRepositoryPort); // Inyecta el mock en el servicio real.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void deleteByIdShouldDeleteWhenTechnologyExists() { // Verifica el flujo exitoso de eliminacion.
        when(technologyRepositoryPort.existsById(eq(1L))).thenReturn(Mono.just(true)); // El repositorio responde que el id existe.
        when(technologyRepositoryPort.deleteById(eq(1L))).thenReturn(Mono.empty()); // El borrado no devuelve valor, solo completa.

        StepVerifier.create(deleteTechnologyService.deleteById(1L))
                .verifyComplete(); // Comprueba que el flujo termina correctamente sin errores.

        verify(technologyRepositoryPort).deleteById(1L); // Asegura que realmente se llamo al metodo de borrado.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void deleteByIdShouldFailWhenTechnologyDoesNotExist() { // Verifica el flujo cuando se intenta borrar algo inexistente.
        when(technologyRepositoryPort.existsById(eq(99L))).thenReturn(Mono.just(false)); // El repositorio responde que el id no existe.

        StepVerifier.create(deleteTechnologyService.deleteById(99L))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException
                                && error.getMessage().equals("Technology not found")) // Valida el tipo y mensaje del error emitido.
                .verify(); // Comprueba que el flujo falla como se esperaba.
    }

    @Test // Indica que este metodo es un caso de prueba.
    void deleteByIdShouldFailWhenIdIsInvalid() { // Verifica que el servicio rechace ids no validos.
        StepVerifier.create(deleteTechnologyService.deleteById(0L))
                .expectErrorMatches(error ->
                        error instanceof ValidationException
                                && error.getMessage().equals("Technology id must be greater than 0")) // Valida el tipo y mensaje del error emitido.
                .verify(); // Comprueba que el flujo falla como se esperaba.
    }
} // Fin de la clase de pruebas del servicio de eliminacion.
