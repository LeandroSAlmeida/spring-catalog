package com.springlearning.catalog.repositories;

import com.springlearning.catalog.domain.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoyuTests {

    @Autowired
    private ProductRepository repository;
    @Test
    public void deleteShouldDeleteObjectWhenIdExist(){

        Long existingId = 1L;
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

}
