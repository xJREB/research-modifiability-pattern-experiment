package webshop.products.api;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAvailabilityCheckResponse {
	@Min(1)
	private long productId;
	@Min(0)
	private int availableAmount;
	@Min(1)
	private int requestedAmount;

	public ProductAvailabilityCheckResponse() {
		// Jackson deserialization
	}

	public ProductAvailabilityCheckResponse(long productId, int availableAmount, int requestedAmount) {
		this.productId = productId;
		this.availableAmount = availableAmount;
		this.requestedAmount = requestedAmount;
	}

	@JsonProperty
	public long getProductId() {
		return productId;
	}

	@JsonProperty
	public int getAvailableAmount() {
		return availableAmount;
	}

	@JsonProperty
	public int getRequestedAmount() {
		return requestedAmount;
	}

}