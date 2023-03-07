package com.bookSuppliment.server.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductCodeForm {
	
	@NotBlank(message="Das Code wird benötigt")
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
