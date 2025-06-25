package com.nogueira.authentication_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.nogueira.authentication_service.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	UserDetails FindByEmail(String email);
	Boolean existsByEmail(String email);
}
