package com.bookSuppliment.server.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductCodeForm {
	
	@NotBlank(message="Das Code wird benötigt")
	@Size(min=9, max=9, message="Das Code muss 9 Briefe sein")
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
