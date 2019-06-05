package webshop.products.api;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewProductMailRequest {
	private long id;
	@NotEmpty
	private String type;
	@NotNull
	private Product product;

	public NewProductMailRequest() {
		// Jackson deserialization
	}

	public NewProductMailRequest(long id, String type, Product product) {
		this.id = id;
		// type needs to be `NEW_PRODUCT_MAIL` for the EmailClient to send the mail
		this.type = type;
		this.product = product;
	}

	public NewProductMailRequest(String type, Product product) {
		// type needs to be `NEW_PRODUCT_MAIL` for the EmailClient to send the mail
		this.type = type;
		this.product = product;
	}

	@JsonProperty
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JsonProperty
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}