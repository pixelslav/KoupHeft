package com.bookSuppliment.server.app.component;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bookSuppliment.server.app.entity.Product;

// It's only for using in HTML templates with Thymeleaf!

// Be careful with method naming.
// If the name is changed, the names must also be changed in the HTML templates in which this class is used.
@Component
public class CategoryFilterComponent {
	
	public boolean isExistSuchProduct(Collection<Product> products, String simpleClassName) {
		boolean result = products.stream()
				.anyMatch(p -> p.getClass().getName().endsWith(simpleClassName));
				
		return result;
	}
	
	public List<Product> filterProducts(Collection<Product> products, String simpleClassName) {
		List<Product> allSubclassProducts = products.stream()
				.filter(p -> p.getClass().getName().endsWith(simpleClassName))
				.toList();
				
		return allSubclassProducts;
	}
}
