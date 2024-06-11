package com.springlearning.catalog.services;

import com.springlearning.catalog.domain.Role;
import com.springlearning.catalog.domain.User;
import com.springlearning.catalog.dto.RoleDTO;
import com.springlearning.catalog.dto.UserDTO;
import com.springlearning.catalog.repositories.RoleRepository;
import com.springlearning.catalog.repositories.UserRepository;
import com.springlearning.catalog.services.exceptions.DatabaseException;
import com.springlearning.catalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
       Page<User> list = repository.findAll(pageable);
       return list.map(x -> new UserDTO(x));
    }
    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        User result = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new UserDTO(result);
    }
    @Transactional
    public UserDTO insert(UserDTO dto){
        User entity = new User();
        copyDtotoEntity(dto,entity);
        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id,UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtotoEntity(dto,entity);
            entity = repository.save(entity);
            return new UserDTO(entity);
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


    private void copyDtotoEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        for (RoleDTO roleDto: dto.getRoles()){
            Role role = roleRepository.getReferenceById(roleDto.getId());
            entity.getRoles().add(role);
        }

    }
}
