package webshop.products.api;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAvailabilityCheckResponse {
	@Min(1)
	private long productId;
	private boolean available;
	@Min(1)
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