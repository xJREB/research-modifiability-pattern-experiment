package webshop.notifications;

import java.util.EnumSet;
import java.util.concurrent.ExecutorService;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import webshop.notifications.db.MailRepository;
import webshop.notifications.health.StandardHealthCheck;
import webshop.notifications.messaging.KafkaListener;
import webshop.notifications.resources.NotificationResource;

public class ServiceApplication extends Application<ServiceConfiguration> {

	public static void main(final String[] args) throws Exception {
		new ServiceApplication().run(args);
	}

	private MailRepository mailRepository;

	@Override
	public String getName() {
		return "NotificationSrv";
	}

	@Override
	public void initialize(final Bootstrap<ServiceConfiguration> bootstrap) {
		this.mailRepository = new MailRepository();
	}

	@Override
	public void run(final ServiceConfiguration configuration, final Environment environment) {
		final EmailClient mailClient = new EmailClient();
		final Client restClient = new JerseyClientBuilder(environment)
				.using(configuration.getJerseyClientConfiguration()).build(getName());
		final NotificationResource notificationResource = new NotificationResource(restClient, mailClient, mailRepository);

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
		executorService.execute(new KafkaListener(notificationResource));

		environment.healthChecks().register("template", healthCheck);
		environment.jersey().register(notificationResource);
		// Register OpenAPI endpoint
		environment.jersey().register(new OpenApiResource());
	}

}
