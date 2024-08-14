package com.springlearning.catalog.repositories;

import com.springlearning.catalog.domain.Product;
import com.springlearning.catalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM (
            SELECT DISTINCT tb_product.id, tb_product.name
            FROM tb_product
            INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
            WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """, countQuery = """
            SELECT COUNT(*) FROM (
            SELECT DISTINCT tb_product.id, tb_product.name
                        FROM tb_product
                        INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
                        WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
                        AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
                        ORDER BY tb_product.name
            ) AS tb_result
            """)
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id IN :productIds")
    List<Product> searchProductWithCategories(List<Long> productIds);
}
