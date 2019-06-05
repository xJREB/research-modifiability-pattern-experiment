package webshop.products.health;

import com.codahale.metrics.health.HealthCheck;

public class StandardHealthCheck extends HealthCheck {
	

	public StandardHealthCheck() {
		
	}

	@Override
	protected Result check() throws Exception {
	
		return Result.healthy();
	}
}