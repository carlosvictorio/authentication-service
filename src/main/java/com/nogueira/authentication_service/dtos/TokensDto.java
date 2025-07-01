package com.nogueira.authentication_service.dtos;

public record TokensDto(
		String accessToken,
		String refreshToken) {
}
