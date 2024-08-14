package com.springlearning.catalog.services;

import com.springlearning.catalog.domain.Category;
import com.springlearning.catalog.domain.Product;
import com.springlearning.catalog.dto.CategoryDTO;
import com.springlearning.catalog.dto.ProductDTO;
import com.springlearning.catalog.projections.ProductProjection;
import com.springlearning.catalog.repositories.CategoryRepository;
import com.springlearning.catalog.repositories.ProductRepository;
import com.springlearning.catalog.services.exceptions.DatabaseException;
import com.springlearning.catalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable){
       Page<Product> list = repository.findAll(pageable);
       return list.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Product result = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new ProductDTO(result, result.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();
        copyDtotoEntity(dto,entity);
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id,ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtotoEntity(dto,entity);
            entity = repository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado" + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }


    private void copyDtotoEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();
        for (CategoryDTO catDto: dto.getCategories()){
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }

    }
    @Transactional(readOnly = true)
    public Page<ProductDTO> testFindAllPagedWithParam(String name, String categoryId, Pageable pageable) {

        List<Long> categoryIds = Arrays.asList();
        if (!"0".equals(categoryId)) {
            categoryIds = Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();
        }

        Page<ProductProjection> page = repository.searchProducts(categoryIds,name, pageable);
        List<Long> productIds = page.map(x -> x.getId()).toList();

        List<Product> entities = repository.searchProductWithCategories(productIds);
        List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

        Page<ProductDTO> pageDto = new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());

        return pageDto;
    }
}
