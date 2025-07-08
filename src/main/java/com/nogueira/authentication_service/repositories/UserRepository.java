package com.nogueira.authentication_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nogueira.authentication_service.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
	Boolean existsByEmail(String email);
}
