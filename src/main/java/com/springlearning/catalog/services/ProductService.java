package com.springlearning.catalog.services;

import com.springlearning.catalog.domain.Product;
import com.springlearning.catalog.dto.ProductDTO;
import com.springlearning.catalog.repositories.ProductRepository;
import com.springlearning.catalog.services.exceptions.DatabaseException;
import com.springlearning.catalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
       Page<Product> list = repository.findAll(pageRequest);
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
        //entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }
    @Transactional
    public ProductDTO update(Long id,ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            //entity.setName(dto.getName());
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
}
