package com.springlearning.catalog.services;

import com.springlearning.catalog.domain.Category;
import com.springlearning.catalog.dto.CategoryDTO;
import com.springlearning.catalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
       List<Category> list = repository.findAll();
       return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.get();
        return new CategoryDTO(entity);
    }


}
