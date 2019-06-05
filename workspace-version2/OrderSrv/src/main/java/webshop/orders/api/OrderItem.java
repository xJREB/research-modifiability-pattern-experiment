package webshop.orders.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem {
	private long productId;
	private int amount;

	public OrderItem() {
		// Jackson deserialization
	}

	public OrderItem(long productId, int amount) {
		this.productId = productId;
		this.amount = amount;
	}

	@JsonProperty
	public long getProductId() {
		return productId;
	}

	@JsonProperty
	public int getAmount() {
		return amount;
	}
}