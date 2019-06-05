package webshop.notifications.resources;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import webshop.notifications.EmailClient;
import webshop.notifications.api.BaseResponse;
import webshop.notifications.api.Customer;
import webshop.notifications.api.MarketingMailRequest;
import webshop.notifications.api.NewProductMailRequest;
import webshop.notifications.api.Order;
import webshop.notifications.api.Product;
import webshop.notifications.db.MailRepository;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class NotificationResource {
	private Client restClient;
	private EmailClient mailClient;
	private MailRepository mailRepository;
	private Logger log;

	public NotificationResource(Client restClient, EmailClient mailClient, MailRepository repository) {
		this.restClient = restClient;
		this.mailClient = mailClient;
		this.mailRepository = repository;
		this.log = LoggerFactory.getLogger(NotificationResource.class);
		log.info("NotificationResource instantiated...");
	}

	// Marketing mails
	@Path("/marketing-mails")
	@GET
	@Timed
	public List<MarketingMailRequest> getMarketingMails(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<MarketingMailRequest> mails = mailRepository.searchMarketingMails(limit.get());

		return mails;
	}

	@Path("/marketing-mails/{id}")
	@GET
	@Timed
	public MarketingMailRequest getMarketingMailById(@PathParam("id") LongParam mailId) {
		final MarketingMailRequest mail = mailRepository.getMarketingMailById(mailId.get());

		if (mail == null) {
			final String msg = String.format("Mail with ID %d does not exist...", mailId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return mail;
	}

	@Path("/marketing-mails")
	@POST
	@Timed
	public BaseResponse sendMarketingMail(@NotNull @Valid MarketingMailRequest request) {
		BaseResponse response;
		final Order order = request.getOrder();
		// type needs to be `SIMILAR_PRODUCTS_MAIL` for the EmailClient to send the mail
		final String mailType = request.getType();

		// Retrieve customer from CustomerSrv
		final String customerUrl = "http://localhost:8000/customers/" + order.getCustomerId();
		final Invocation.Builder customerRequest = restClient.target(customerUrl).request();
		final Customer customer = customerRequest.get(Customer.class);

		log.info("Trying to send marketing mail to customer " + customer.getName() + " for order with ID "
				+ order.getId() + "...");
		if (mailClient.sendMarketingMail(mailType, customer, order)) {
			response = new BaseResponse("OK", 201, "Marketing mail successfully sent.");
			log.info("Marketing mail successfully sent.");
			mailRepository.storeMarketingMail(request);
		} else {
			response = new BaseResponse("FAILURE", 400, "Unknown mail type. Marketing mail could not be sent.");
			log.info("Unknown mail type. Marketing mail could not be sent.");
		}

		return response;
	}

	// Product mails

	@Path("/product-mails")
	@GET
	@Timed
	public List<NewProductMailRequest> getProductMails(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<NewProductMailRequest> mails = mailRepository.searchProductMails(limit.get());

		return mails;
	}

	@Path("/product-mails/{id}")
	@GET
	@Timed
	public NewProductMailRequest getProductMailById(@PathParam("id") LongParam mailId) {
		final NewProductMailRequest mail = mailRepository.getProductMailById(mailId.get());

		if (mail == null) {
			final String msg = String.format("Product mail with ID %d does not exist...", mailId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return mail;
	}

	@Path("/product-mails")
	@POST
	@Timed
	public BaseResponse sendProductMail(@NotNull @Valid NewProductMailRequest request) {
		BaseResponse response;
		final Product product = request.getProduct();
		// type needs to be `NEW_PRODUCT_MAIL` for the EmailClient to send the mail
		final String mailType = request.getType();

		log.info("Trying to send new product mail to sales department for product with ID " + product.getId() + "...");
		if (mailClient.sendNewProductMail(mailType, product)) {
			response = new BaseResponse("OK", 201, "Product mail successfully sent.");
			log.info("Product mail successfully sent.");
			mailRepository.storeProductMail(request);
		} else {
			response = new BaseResponse("FAILURE", 400, "Unknown mail type. Product mail could not be sent.");
			log.info("Unknown mail type. Product mail could not be sent.");
		}

		return response;
	}

	// New products DB

	@Path("/new-products")
	@GET
	@Timed
	public List<Product> getNewProducts(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<Product> newProducts = mailRepository.searchNewProducts(limit.get());

		return newProducts;
	}

	@Path("/new-products/{id}")
	@GET
	@Timed
	public Product getNewProductById(@PathParam("id") LongParam productId) {
		final Product newProduct = mailRepository.getNewProductById(productId.get());

		if (newProduct == null) {
			final String msg = String.format("New product with ID %d does not exist...", productId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return newProduct;
	}

	@Path("/new-products")
	@POST
	@Timed
	public BaseResponse addNewProduct(@NotNull @Valid Product product) {
		mailRepository.storeNewProduct(product);
		BaseResponse response = new BaseResponse("OK", 201, "New product successfully added to DB.");
		log.info("New product successfully added to DB.");

		return response;
	}
}