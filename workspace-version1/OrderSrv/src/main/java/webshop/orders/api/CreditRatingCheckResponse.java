package webshop.orders.api;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditRatingCheckResponse {
	@Min(1)
	private long customerId;
	private boolean acceptable;

	public CreditRatingCheckResponse() {
		// Jackson deserialization
	}

	public CreditRatingCheckResponse(long customerId, boolean acceptable) {
		this.customerId = customerId;
		this.acceptable = acceptable;
	}

	@JsonProperty
	public long getCustomerId() {
		return customerId;
	}

	@JsonProperty
	public boolean getAcceptable() {
		return acceptable;
	}

}