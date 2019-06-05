package webshop.customers;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import webshop.customers.db.CustomerRepository;
import webshop.customers.health.StandardHealthCheck;
import webshop.customers.resources.CustomerResource;

public class ServiceApplication extends Application<ServiceConfiguration> {

	public static void main(final String[] args) throws Exception {
		new ServiceApplication().run(args);
	}

	private CustomerRepository customerRepository;

	@Override
	public String getName() {
		return "CustomerSrv";
	}

	@Override
	public void initialize(final Bootstrap<ServiceConfiguration> bootstrap) {
		this.customerRepository = new CustomerRepository();
	}

	@Override
	public void run(final ServiceConfiguration configuration, final Environment environment) {

		final CustomerResource customerResource = new CustomerResource(configuration.getDefaultCreditRating(),
				customerRepository);

		final StandardHealthCheck healthCheck = new StandardHealthCheck(configuration.getDefaultCreditRating());

		// JSON pretty print
		ObjectMapper mapper = environment.getObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		// CORS filter
		final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		cors.setInitParameter("allowedOrigins", "*");
		cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
		cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
		cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		environment.healthChecks().register("template", healthCheck);
		environment.jersey().register(customerResource);
		// Register OpenAPI endpoint
		environment.jersey().register(new OpenApiResource());
	}

}
