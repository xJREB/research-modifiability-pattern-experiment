package webshop.warehouse;

import java.util.EnumSet;
import java.util.concurrent.ExecutorService;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import webshop.warehouse.db.WarehouseRepository;
import webshop.warehouse.health.StandardHealthCheck;
import webshop.warehouse.messaging.KafkaListener;
import webshop.warehouse.resources.WarehouseResource;

public class ServiceApplication extends Application<ServiceConfiguration> {

	public static void main(final String[] args) throws Exception {
		new ServiceApplication().run(args);
	}

	private WarehouseRepository warehouseRepository;

	@Override
	public String getName() {
		return "WarehouseSrv";
	}

	@Override
	public void initialize(final Bootstrap<ServiceConfiguration> bootstrap) {
		this.warehouseRepository = new WarehouseRepository();
	}

	@Override
	public void run(final ServiceConfiguration configuration, final Environment environment) {

		final WarehouseResource warehouseResource = new WarehouseResource(warehouseRepository);

		final StandardHealthCheck healthCheck = new StandardHealthCheck();

		// JSON pretty print
		ObjectMapper mapper = environment.getObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		// CORS filter
		final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		cors.setInitParameter("allowedOrigins", "*");
		cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
		cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
		cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		// Instantiate Kafka consumer and bind it to environment
		final ExecutorService executorService = environment.lifecycle().executorService("kafka-threads").minThreads(2)
				.maxThreads(10).build();
		executorService.execute(new KafkaListener(warehouseResource));

		environment.healthChecks().register("template", healthCheck);
		environment.jersey().register(warehouseResource);
		// Register OpenAPI endpoint
		environment.jersey().register(new OpenApiResource());
	}

}
