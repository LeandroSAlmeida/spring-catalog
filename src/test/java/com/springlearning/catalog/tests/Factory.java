package com.springlearning.catalog.tests;

import com.springlearning.catalog.domain.Category;
import com.springlearning.catalog.domain.Product;
import com.springlearning.catalog.dto.ProductDTO;

import java.time.Instant;

public class Factory {
    public static Product createProduct(){
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-10-20t03:00:00Z"));
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
