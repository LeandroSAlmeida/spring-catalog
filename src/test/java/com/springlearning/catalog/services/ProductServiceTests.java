package com.springlearning.catalog.services;

import com.springlearning.catalog.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
    }




    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        // Configurando o mock para retornar true para existsById com o ID existente
        Mockito.when(repository.existsById(existingId)).thenReturn(true);

        // Testando o método delete
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        // Verificando se o método delete foi chamado com o ID correto
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }
}

