package com.springlearning.catalog.repositories;

import com.springlearning.catalog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
