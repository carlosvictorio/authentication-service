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
import com.nogueira.authentication_service.dtos.RegisterUserDto;
import com.nogueira.authentication_service.dtos.TokensDto;
import com.nogueira.authentication_service.services.UserAuthService;

@RestController
@RequestMapping("/auth")
public class UserAuthController {
	
	@Autowired
	private UserAuthService userService;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterUserDto user) {
		userService.register(user);
		return ResponseEntity.status(201).body("User registered successfully!");
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokensDto> login(@RequestBody LoginUserDto user) {
		TokensDto tokens = userService.login(user);
		return ResponseEntity.ok(tokens);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<AccessTokenDto> refresh(@RequestBody String refreshToken) {
		AccessTokenDto accessToken = userService.refresh(refreshToken);
		return ResponseEntity.ok(accessToken);
	}
	
	@PostMapping("/status/active")
	public ResponseEntity<String> activateStatus(@RequestBody EmailDto email) {
		userService.activateStatus(email);
		return ResponseEntity.ok("Status updated successfully!");
	}
	
	@PostMapping("/status/pending")
	public ResponseEntity<String> pendingStatus(@RequestBody EmailDto email) {
		userService.pendingStatus(email);
		return ResponseEntity.ok("Status updated successfully!");
	}

}
