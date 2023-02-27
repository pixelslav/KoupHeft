package com.bookSuppliment.server.app.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "books")
public class Book implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String title;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "book_user",
			   joinColumns = @JoinColumn(name="book_id"),
			   inverseJoinColumns = @JoinColumn(name="user_id"))
	private Set<User> users = new HashSet<>();
	
	public void addUser(User user) {
		this.users.add(user);
		user.getBooks().add(this);
	}
	
	public void removeUser(User user) {
		this.users.remove(user);
		user.getBooks().remove(this);
	}
}
