package webshop.orderprocess.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditRatingCheckResponse {
	@Min(1)
	private long customerId;
	@Min(1)
	@Max(6)
	// 1 --> best rating
	// 6 --> worst rating
	private int rating;

	public CreditRatingCheckResponse() {
		// Jackson deserialization
	}

	public CreditRatingCheckResponse(long customerId, int rating) {
		this.customerId = customerId;
		this.rating = rating;
	}

	@JsonProperty
	public long getCustomerId() {
		return customerId;
	}

	@JsonProperty
	public int getRating() {
		return rating;
	}

}