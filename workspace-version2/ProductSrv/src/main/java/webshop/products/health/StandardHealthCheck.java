package webshop.products.health;

import com.codahale.metrics.health.HealthCheck;

public class StandardHealthCheck extends HealthCheck {
	private final long defaultCategoryId;

	public StandardHealthCheck(long defaultCategoryId) {
		this.defaultCategoryId = defaultCategoryId;
	}

	@Override
	protected Result check() throws Exception {
		if (defaultCategoryId == 0) {
			return Result.unhealthy("No valid default categoryId found...");
		}
		return Result.healthy();
	}
}