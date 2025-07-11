package com.nogueira.authentication_service.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.nogueira.authentication_service.exceptions.UserUnauthorizedException;
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
		} catch (TokenExpiredException e) {
	        throw new UserUnauthorizedException("Access token expired.");
	    } catch (SignatureVerificationException e) {
	        throw new UserUnauthorizedException("Invalid access token signature.");
	    } catch (JWTDecodeException e) {
	        throw new UserUnauthorizedException("Malformed access token.");
	    } catch (JWTVerificationException e) {
	        throw new UserUnauthorizedException("Invalid access token.");
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
	    } catch (TokenExpiredException e) {
	    	throw new UserUnauthorizedException("Refresh token expired.");
	    } catch (SignatureVerificationException e) {
	    	throw new UserUnauthorizedException("Invalid refresh token signature.");
	    } catch (JWTDecodeException e) {
	    	throw new UserUnauthorizedException("Malformed refresh token.");
	    } catch (JWTVerificationException e) {
	    	throw new UserUnauthorizedException("Invalid refresh token.");
	    }
	}
	
	private Instant generateRefreshInstant() {
	    return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));
	}

}
