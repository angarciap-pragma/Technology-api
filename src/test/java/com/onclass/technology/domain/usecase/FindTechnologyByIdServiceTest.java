package com.onclass.technology.domain.usecase;

import com.onclass.technology.application.service.FindTechnologyByIdService;
import com.onclass.technology.domain.exception.NotFoundException;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTechnologyByIdServiceTest {

    @Mock
    private TechnologyRepositoryPort technologyRepositoryPort;

    private FindTechnologyByIdService findTechnologyByIdService;

    @BeforeEach
    void setUp() {
        findTechnologyByIdService = new FindTechnologyByIdService(technologyRepositoryPort);
    }

    @Test
    void findByIdShouldReturnTechnologyWhenExists() {
        when(technologyRepositoryPort.findById(eq(1L)))
                .thenReturn(Mono.just(Technology.rehydrate(1L, "Java", "Backend language")));

        StepVerifier.create(findTechnologyByIdService.findById(1L))
                .expectNextMatches(technology ->
                        technology.getId().equals(1L)
                                && technology.getName().equals("Java"))
                .verifyComplete();
    }

    @Test
    void findByIdShouldFailWhenTechnologyDoesNotExist() {
        when(technologyRepositoryPort.findById(eq(99L))).thenReturn(Mono.empty());

        StepVerifier.create(findTechnologyByIdService.findById(99L))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException
                                && error.getMessage().equals("Technology not found"))
                .verify();
    }

    @Test
    void findByIdShouldFailWhenIdIsInvalid() {
        StepVerifier.create(findTechnologyByIdService.findById(0L))
                .expectErrorMatches(error ->
                        error instanceof ValidationException
                                && error.getMessage().equals("Technology id must be greater than 0"))
                .verify();
    }
}
