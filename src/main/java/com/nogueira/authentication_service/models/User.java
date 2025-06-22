package com.nogueira.authentication_service.models;

import com.nogueira.authentication_service.enums.RoleEnum;
import com.nogueira.authentication_service.enums.StatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private RoleEnum role;
	@Enumerated(EnumType.STRING)
	private StatusEnum status;
	
	public User(){
	}
	
	public User(String name, String email, String password, RoleEnum role, StatusEnum status) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = status;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public RoleEnum getRole() {
		return role;
	}
	
	public void setRole(RoleEnum role) {
		this.role = role;
	}
	
	public StatusEnum getStatus() {
		return status;
	}
	
	public void setStatus(StatusEnum status) {
		this.status = status;
	}
	
}
