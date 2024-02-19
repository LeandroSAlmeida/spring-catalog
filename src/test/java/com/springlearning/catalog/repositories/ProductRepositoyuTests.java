package com.springlearning.catalog.repositories;

import com.springlearning.catalog.domain.Product;
import com.springlearning.catalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoyuTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long countTotalProducts;


    @BeforeEach
    void setUp() throws Exception{
        Long existingId = 1L;
        countTotalProducts = 25L;
    }
    @Test
    public void saveSholdPersistWithAutoIncrementWhenIdIsNull(){

        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());

    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExist(){

        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

}
