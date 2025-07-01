package com.nogueira.authentication_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nogueira.authentication_service.dtos.AccessTokenDto;
import com.nogueira.authentication_service.dtos.LoginUserDto;
import com.nogueira.authentication_service.dtos.RegisterUserDto;
import com.nogueira.authentication_service.dtos.TokensDto;
import com.nogueira.authentication_service.enums.StatusEnum;
import com.nogueira.authentication_service.models.User;
import com.nogueira.authentication_service.repositories.UserRepository;

@Service
public class UserAuthService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public void register(RegisterUserDto user) {
		if(userRepository.existsByEmail(user.email())) {
			throw new RuntimeException("User already registered!");
		}
		String encryptedPassword = passwordEncoder.encode(user.password());
		User newUser = new User(user.name(), user.email(), encryptedPassword);
		userRepository.save(newUser);
	}
	
	public void activateStatus(String email) {
		if(!userRepository.existsByEmail(email)) {
			throw new RuntimeException("User not exists!");
		}
		User user = userRepository.findByUsername(email);
		user.setStatus(StatusEnum.ACTIVE);
		userRepository.save(user);
	}
	
	public void pendingStatus(String email) {
		if(!userRepository.existsByEmail(email)) {
			throw new RuntimeException("User not exists!");
		}
		User user = userRepository.findByUsername(email);
		user.setStatus(StatusEnum.PENDING_PAYMENT);
		userRepository.save(user);
	}
	
	public TokensDto login(LoginUserDto user) {
		User userFound = userRepository.findByUsername(user.email());
		if(userFound.getStatus() != StatusEnum.ACTIVE) throw new RuntimeException("pending payment!");
		
		try {
		var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.password());
		var auth = authenticationManager.authenticate(usernamePassword);
		var accessToken = tokenService.generateAccessToken((User)auth.getPrincipal());
		var refreshToken = tokenService.generateRefreshToken((User)auth.getPrincipal());
		return new TokensDto(accessToken, refreshToken);
		
		}catch(BadCredentialsException e) {
			throw new BadCredentialsException("Credenciais inválidas: ", e);
		}catch(Exception e) {
			throw new RuntimeException("Erro inesperado: " + e.getMessage());
		}
	}
	
	public AccessTokenDto refresh(String refreshToken) {
		String username = tokenService.validateRefreshToken(refreshToken);
		User user = userRepository.findByUsername(username);
		String newAccessToken = tokenService.generateAccessToken(user);
		return new AccessTokenDto(newAccessToken);
	}

}
