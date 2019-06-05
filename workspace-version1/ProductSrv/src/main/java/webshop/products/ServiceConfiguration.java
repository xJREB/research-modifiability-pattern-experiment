package webshop.products;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

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

	@Valid
	@NotNull
	private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

	@JsonProperty("jerseyClient")
	public JerseyClientConfiguration getJerseyClientConfiguration() {
		return jerseyClient;
	}

	@JsonProperty("jerseyClient")
	public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
		this.jerseyClient = jerseyClient;
	}
}
