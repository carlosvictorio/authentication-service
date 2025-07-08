package com.nogueira.authentication_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDto(@NotBlank @Email String email) {
}
