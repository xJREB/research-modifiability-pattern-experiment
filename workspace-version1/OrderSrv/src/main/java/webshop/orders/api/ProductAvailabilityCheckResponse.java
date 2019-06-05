package webshop.orders.api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAvailabilityCheckResponse {
	@NotNull
	@Min(1)
	private long productId;
	@NotNull
	private boolean available;
	@NotNull
	@Min(0)
	private int requestedAmount;

	public ProductAvailabilityCheckResponse() {
		// Jackson deserialization
	}

	public ProductAvailabilityCheckResponse(long productId, boolean available, int requestedAmount) {
		this.productId = productId;
		this.available = available;
		this.requestedAmount = requestedAmount;
	}

	@JsonProperty
	public long getProductId() {
		return productId;
	}

	@JsonProperty
	public boolean getAvailable() {
		return available;
	}

	@JsonProperty
	public int getRequestedAmount() {
		return requestedAmount;
	}

}