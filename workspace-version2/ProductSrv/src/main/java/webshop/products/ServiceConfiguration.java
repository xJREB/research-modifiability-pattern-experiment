package webshop.products;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class ServiceConfiguration extends Configuration {

	@NotNull
	@Min(1)
	private long defaultCategoryId = 1;

	@JsonProperty
	public void setDefaultCategoryId(long id) {
		this.defaultCategoryId = id;
	}

	@JsonProperty
	public long getDefaultCategoryId() {
		return this.defaultCategoryId;
	}
}
