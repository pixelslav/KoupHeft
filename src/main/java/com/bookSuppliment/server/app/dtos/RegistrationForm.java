package com.bookSuppliment.server.app.dtos;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class RegistrationForm implements Serializable{
	
	@NotBlank(message="Der Name wird benötigt")
	private String name;
	
	@NotBlank(message="Die E-mail Adresse wird benötigt")
	@Email(message="Die E-Mail Adresse muss E-Mail-Format haben")
	private String email;
	
	@NotBlank(message="Das Password wird benötigt")
	@Size(min=8, message="Das Password muss mehr als 8 Briefe sein")
//	@Pattern(regexp="^[A-Z0-9]+$", message="Das Passwort muss einen Großbuchstaben und eine Zahl enthalten")
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
