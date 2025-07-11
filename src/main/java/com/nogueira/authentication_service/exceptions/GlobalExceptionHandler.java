package com.nogueira.authentication_service.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	    @ExceptionHandler(BadCredentialsException.class)
	    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas: " + e.getMessage());
	    }

	    @ExceptionHandler(UserAlreadyExistsException.class)
	    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	    }

	    @ExceptionHandler(UserNotFoundException.class)
	    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	 	    
	    @ExceptionHandler(UserUnauthorizedException.class)
	    public ResponseEntity<String> UserUnauthorizedException(UserUnauthorizedException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	    }

	    @ExceptionHandler(EntityNotFoundException.class)
	    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	    
	    @ExceptionHandler(HttpMessageNotReadableException.class)
	    public ResponseEntity<?> handleJsonParseError(HttpMessageNotReadableException ex) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Malformed JSON: " + "Request body is malformed or incomplete."));
	    }

	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
	        Map<String, String> errors = new HashMap<>();
	        ex.getBindingResult().getFieldErrors().forEach(error -> {
	            errors.put(error.getField(), error.getDefaultMessage());
	        });
	        return ResponseEntity.badRequest().body(errors);
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<String> handleUnexpectedException(Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Unexpected error: " + e.getLocalizedMessage());
	    }
	    
}
