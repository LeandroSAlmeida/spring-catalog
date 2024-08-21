package com.springlearning.catalog.repositories;

import com.springlearning.catalog.domain.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {

}
