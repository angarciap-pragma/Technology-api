package com.onclass.technology.domain.usecase;

import com.onclass.technology.application.service.DeleteTechnologyService;
import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTechnologyServiceTest {

    @Mock
    private TechnologyRepositoryPort technologyRepositoryPort;

    private DeleteTechnologyService deleteTechnologyService;

    @BeforeEach
    void setUp() {
        deleteTechnologyService = new DeleteTechnologyService(technologyRepositoryPort);
    }

    @Test
    void deleteByIdShouldDeleteWhenTechnologyExists() {
        when(technologyRepositoryPort.existsById(eq(1L))).thenReturn(Mono.just(true));
        when(technologyRepositoryPort.deleteById(eq(1L))).thenReturn(Mono.empty());

        StepVerifier.create(deleteTechnologyService.deleteById(1L))
                .verifyComplete();

        verify(technologyRepositoryPort).deleteById(1L);
    }

    @Test
    void deleteByIdShouldFailWhenTechnologyDoesNotExist() {
        when(technologyRepositoryPort.existsById(eq(99L))).thenReturn(Mono.just(false));

        StepVerifier.create(deleteTechnologyService.deleteById(99L))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException
                                && error.getMessage().equals("Technology not found"))
                .verify();
    }

    @Test
    void deleteByIdShouldFailWhenIdIsInvalid() {
        StepVerifier.create(deleteTechnologyService.deleteById(0L))
                .expectErrorMatches(error ->
                        error instanceof ValidationException
                                && error.getMessage().equals("Technology id must be greater than 0"))
                .verify();
    }
}
