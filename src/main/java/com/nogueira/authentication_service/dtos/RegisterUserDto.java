package com.nogueira.authentication_service.dtos;

import com.nogueira.authentication_service.enums.RoleEnum;
import com.nogueira.authentication_service.enums.StatusEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserDto(	 	
		@NotBlank String name,
	    @NotBlank @Email String email,
	    @NotBlank String password,
	    @NotBlank RoleEnum role,
	    @NotBlank StatusEnum status) {
}
