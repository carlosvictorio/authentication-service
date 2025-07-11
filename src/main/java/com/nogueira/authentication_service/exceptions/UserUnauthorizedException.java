package com.nogueira.authentication_service.exceptions;

public class UserUnauthorizedException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UserUnauthorizedException(String msg) {
		super(msg);
	}
}
