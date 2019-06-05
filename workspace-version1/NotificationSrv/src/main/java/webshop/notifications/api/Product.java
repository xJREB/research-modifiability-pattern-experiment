package webshop.notifications.api;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
	private long id;
	@NotEmpty
	private String name;
	@Min(1)
	private long categoryId;
	@DecimalMin("0.01")
	private double price;

	public Product() {
		// Jackson deserialization
	}

	public Product(long id, String name, long categoryId, double price) {
		this.id = id;
		this.name = name;
		this.categoryId = categoryId;
		this.price = price;
	}

	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty
	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	@JsonProperty
	public double getPrice() {
		return price;
	}
}