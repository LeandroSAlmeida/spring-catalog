package com.springlearning.catalog.services;

import com.springlearning.catalog.domain.Product;
import com.springlearning.catalog.dto.ProductDTO;
import com.springlearning.catalog.repositories.ProductRepository;
import com.springlearning.catalog.services.exceptions.DatabaseException;
import com.springlearning.catalog.services.exceptions.ResourceNotFoundException;
import com.springlearning.catalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;


    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

    }

    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){
        Assertions.assertThrows(DatabaseException.class, () ->{
            service.delete(dependentId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class,()-> {
            service.delete(nonExistingId);
        });
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

