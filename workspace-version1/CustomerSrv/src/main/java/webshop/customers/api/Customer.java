package webshop.customers.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
	private long id;
	@NotEmpty
	private String name;
	@NotEmpty
	@Email
	private String email;
	@NotNull
	@Min(1)
	@Max(6)
	// 1 --> best rating
	// 6 --> worst rating
	private int creditRating;

	public Customer() {
		// Jackson deserialization
	}

	public Customer(long id, String name, String email, int creditRating) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.creditRating = creditRating;
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
	public String getEmail() {
		return email;
	}

	@JsonProperty
	public int getCreditRating() {
		return creditRating;
	}

	public void setCreditRating(int rating) {
		this.creditRating = rating;
	}

}