package com.nogueira.authentication_service.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nogueira.authentication_service.models.User;

@Service
public class TokenService {
	
	@Value("${SECRET}")
	private String accessSecret;
	@Value("${REFRESH_SECRET}")
	private String refreshSecret;
	
	public String generateAccessToken(User user) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(accessSecret);
			
			String token = JWT.create()
					.withIssuer("access")
					.withSubject(user.getUsername())
					.withExpiresAt(generateInstant())
					.sign(algorithm);
			
			return token;
		} catch(JWTCreationException e) {
			throw new RuntimeException("Error while generating token: ", e);
		}
	}
	
	public String validateAccessToken(String token) {
		
		try {
			Algorithm algorithm = Algorithm.HMAC256(accessSecret);
			return JWT.require(algorithm)
					.withIssuer("access")
					.build()
					.verify(token)
					.getSubject();
		} catch(JWTVerificationException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private Instant generateInstant() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
	
	public String generateRefreshToken(User user) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(refreshSecret);
			
			String token = JWT.create()
					.withIssuer("refresh")
					.withSubject(user.getUsername())
					.withExpiresAt(generateRefreshInstant())
					.sign(algorithm);
			
			return token;
		} catch(JWTCreationException e) {
			throw new RuntimeException("Error while generating refresh token: ", e);
		}
	}
	
	public String validateRefreshToken(String token) {
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(refreshSecret);
	        return JWT.require(algorithm)
	                .withIssuer("refresh")
	                .build()
	                .verify(token)
	                .getSubject();
	    } catch (JWTVerificationException e) {
	        throw new RuntimeException(e.getMessage());
	    }
	}
	
	private Instant generateRefreshInstant() {
	    return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));
	}

}
