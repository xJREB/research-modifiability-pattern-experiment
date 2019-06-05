package webshop.notifications.api;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketingMailRequest {
	private long id;
	@NotEmpty
	private String type;
	@NotNull
	private Order order;

	public MarketingMailRequest() {
		// Jackson deserialization
	}

	public MarketingMailRequest(long id, String type, Order order) {
		this.id = id;
		// type needs to be `SIMILAR_PRODUCTS_MAIL` for the EmailClient to send the mail
		this.type = type;
		this.order = order;
	}

	public MarketingMailRequest(String type, Order order) {
		// type needs to be `SIMILAR_PRODUCTS_MAIL` for the EmailClient to send the mail
		this.type = type;
		this.order = order;
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
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}