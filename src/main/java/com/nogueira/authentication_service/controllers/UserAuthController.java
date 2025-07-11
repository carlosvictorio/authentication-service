package com.nogueira.authentication_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nogueira.authentication_service.dtos.AccessTokenDto;
import com.nogueira.authentication_service.dtos.EmailDto;
import com.nogueira.authentication_service.dtos.LoginUserDto;
import com.nogueira.authentication_service.dtos.RefreshTokenDto;
import com.nogueira.authentication_service.dtos.RegisterUserDto;
import com.nogueira.authentication_service.dtos.TokensDto;
import com.nogueira.authentication_service.services.UserAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserAuthController {
	
	@Autowired
	private UserAuthService userService;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody @Valid RegisterUserDto user) {
		userService.register(user);
		return ResponseEntity.status(201).body("User registered successfully!");
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokensDto> login(@RequestBody @Valid LoginUserDto user) {
		TokensDto tokens = userService.login(user);
		return ResponseEntity.ok(tokens);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<AccessTokenDto> refresh(@RequestBody RefreshTokenDto refreshToken) {
		AccessTokenDto accessToken = userService.refresh(refreshToken);
		return ResponseEntity.ok(accessToken);
	}
	
	@PostMapping("/status/active")
	public ResponseEntity<String> activateStatus(@RequestBody @Valid EmailDto email) {
		String msg = userService.activateStatus(email);
		return ResponseEntity.ok(msg);
	}
	
	@PostMapping("/status/pending")
	public ResponseEntity<String> pendingStatus(@RequestBody @Valid EmailDto email) {
		String msg = userService.pendingStatus(email);
		return ResponseEntity.ok(msg);
	}

}
