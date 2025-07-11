package com.nogueira.authentication_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nogueira.authentication_service.dtos.AccessTokenDto;
import com.nogueira.authentication_service.dtos.EmailDto;
import com.nogueira.authentication_service.dtos.LoginUserDto;
import com.nogueira.authentication_service.dtos.RefreshTokenDto;
import com.nogueira.authentication_service.dtos.RegisterUserDto;
import com.nogueira.authentication_service.dtos.TokensDto;
import com.nogueira.authentication_service.enums.StatusEnum;
import com.nogueira.authentication_service.exceptions.UserAlreadyExistsException;
import com.nogueira.authentication_service.exceptions.UserNotFoundException;
import com.nogueira.authentication_service.exceptions.UserUnauthorizedException;
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
			throw new UserAlreadyExistsException("User already registered!");
		}
		String encryptedPassword = passwordEncoder.encode(user.password());
		User newUser = new User(user.name(), user.email(), encryptedPassword);
		userRepository.save(newUser);
	}
	
	public String activateStatus(EmailDto email) {
		if(!userRepository.existsByEmail(email.email())) {
			throw new UserNotFoundException("User not exists!");
		}
		
		User user = userRepository.findByEmail(email.email());
		
		if(user.getStatus() == StatusEnum.ACTIVE) {
			return "User status already ACTIVE!";
		}
		
		user.setStatus(StatusEnum.ACTIVE);
		userRepository.save(user);
		return "User status updated successfully!";
	}
	
	public String pendingStatus(EmailDto email) {
		if(!userRepository.existsByEmail(email.email())) {
			throw new UserNotFoundException("User not exists!");
		}
		User user = userRepository.findByEmail(email.email());
		
		if(user.getStatus() == StatusEnum.PENDING_PAYMENT) {
			return "User status already PENDING_PAYMENT!";
		}
		
		user.setStatus(StatusEnum.PENDING_PAYMENT);
		userRepository.save(user);
		return "User status updated successfully!";
	}
	
	public TokensDto login(LoginUserDto user) {
	
		try {
			var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.password());
			var auth = authenticationManager.authenticate(usernamePassword);
		
			User authenticatedUser = (User) auth.getPrincipal();
		 
			if(authenticatedUser.getStatus() != StatusEnum.ACTIVE) throw new UserUnauthorizedException("Pending payment.");
		
			var accessToken = tokenService.generateAccessToken((User)auth.getPrincipal());
			var refreshToken = tokenService.generateRefreshToken((User)auth.getPrincipal());
			return new TokensDto(accessToken, refreshToken);
		
		}catch (UserUnauthorizedException e) {
		    throw e;
		}catch(BadCredentialsException e) {
			throw new BadCredentialsException(e.getMessage());
		}catch(Exception e) {
			throw new RuntimeException("Erro inesperado: " + e.getMessage());
		}
	}
	
	public AccessTokenDto refresh(RefreshTokenDto refreshToken) {
		
		if (refreshToken == null || refreshToken.refreshToken() == null) {
		    throw new UserUnauthorizedException("Invalid refresh token");
		}

		String username = tokenService.validateRefreshToken(refreshToken.refreshToken());
		User user = userRepository.findByEmail(username);
		
		if(user == null) throw new UserUnauthorizedException("User not found for provided token!");
		
		String newAccessToken = tokenService.generateAccessToken(user);
		return new AccessTokenDto(newAccessToken);
	}

}
