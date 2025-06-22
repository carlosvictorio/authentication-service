package com.nogueira.authentication_service.enums;

public enum StatusEnum {
	PENDING_PAYMENT("pending_payment"),
	ACTIVE("active");
		
	private String status;
		
	StatusEnum(String status) {
		this.status = status;
	}
		
	public String getStatus() {
		return status;
	}
}
