package webshop.customers.health;

import com.codahale.metrics.health.HealthCheck;

public class StandardHealthCheck extends HealthCheck {
	private final int defaultCreditRating;

	public StandardHealthCheck(int rating) {
		this.defaultCreditRating = rating;
	}

	@Override
	protected Result check() throws Exception {
		if (defaultCreditRating == 0) {
			return Result.unhealthy("No valid default credit rating found...");
		}
		return Result.healthy();
	}
}