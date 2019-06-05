package webshop.orders.resources;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import webshop.orders.api.BaseResponse;
import webshop.orders.api.CreditRatingCheckResponse;
import webshop.orders.api.MarketingMailRequest;
import webshop.orders.api.Order;
import webshop.orders.api.OrderItem;
import webshop.orders.api.ProductAvailabilityCheckResponse;
import webshop.orders.db.OrderRepository;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
	private OrderRepository orderRepository;
	private Client restClient;
	private Logger log;
	private final String CREDIT_RATING_CHECK_ENDPOINT = "http://localhost:8000";
	private final String PRODUCT_AVAILABILITY_CHECK_ENDPOINT = "http://localhost:8070";

	public OrderResource(OrderRepository repository, Client restClient) {
		this.orderRepository = repository;
		this.restClient = restClient;
		this.log = LoggerFactory.getLogger(OrderResource.class);
		log.info("OrderResource instantiated...");
	}

	@GET
	@Timed
	public List<Order> getOrders(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<Order> orders = orderRepository.search(limit.get());

		return orders;
	}

	@Path("/{id}")
	@GET
	@Timed
	public Order getOrderById(@PathParam("id") LongParam orderId) {
		final Order order = orderRepository.getById(orderId.get());

		if (order == null) {
			final String msg = String.format("Order with ID %d does not exist...", orderId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return order;
	}

	@POST
	@Timed
	public BaseResponse createOrder(@NotNull @Valid Order order) {
		final long customerId = order.getCustomerId();
		final String creditRatingUrl = CREDIT_RATING_CHECK_ENDPOINT + "/customers/" + customerId
				+ "/credit-rating-check";
		final List<OrderItem> items = order.getItems();

		// Check credit rating of customer
		log.info("Checking credit rating of customer with ID " + customerId + "...");
		final Invocation.Builder creditRatingRequest = restClient.target(creditRatingUrl).request();
		final CreditRatingCheckResponse creditRatingResponse = creditRatingRequest.get(CreditRatingCheckResponse.class);

		if (creditRatingResponse.getAcceptable()) {
			// Permitted --> credit rating sufficient
			log.info("Credit rating of customer with ID " + order.getCustomerId()
					+ " acceptable! Checking product availability...");

			// Check availability of all order items
			boolean itemsAvailable = true;
			String productAvailabilityUrl;
			Invocation.Builder productAvailabilityRequest;
			ProductAvailabilityCheckResponse productAvailabilityResponse;
			for (OrderItem item : items) {
				productAvailabilityUrl = PRODUCT_AVAILABILITY_CHECK_ENDPOINT + "/products/" + item.getProductId()
						+ "/availability?amount=" + item.getAmount();
				productAvailabilityRequest = restClient.target(productAvailabilityUrl).request();
				productAvailabilityResponse = productAvailabilityRequest.get(ProductAvailabilityCheckResponse.class);
				if (!productAvailabilityResponse.getAvailable()) {
					log.info(
							"Product with ID " + item.getProductId() + " has not enough capacity to fullfil the order ("
									+ productAvailabilityResponse.getRequestedAmount() + " requested).");
					itemsAvailable = false;
					break;
				}
			}

			if (itemsAvailable) {
				// Items available --> create order
				final Order createdOrder = orderRepository.store(order);
				log.info("Order with ID " + createdOrder.getId() + " successfully created.");

				// Invoking the NotificationSrv to send a SIMILAR_PRODUCTS_MAIL for the new
				// order
				MarketingMailRequest marketingMailRequest = new MarketingMailRequest("SIMILAR_PRODUCTS_MAIL",
						createdOrder);
				Invocation.Builder request = restClient.target("http://localhost:8010/marketing-mails").request();
				request.post(Entity.json(marketingMailRequest), BaseResponse.class);

				// Return final response
				return new BaseResponse("OK", 201, "Order with ID " + createdOrder.getId() + " successfully created.");

			} else {
				// Items not available --> decline order
				throw new WebApplicationException("Order declined for customer with ID " + order.getCustomerId()
						+ ": items not available in required capacity.", Status.BAD_REQUEST);
			}

		} else {
			// Declined --> credit rating too low
			log.info("Order declined for customer with ID " + order.getCustomerId() + ": credit rating too low.");
			throw new WebApplicationException(
					"Order declined for customer with ID " + order.getCustomerId() + ": credit rating too low.",
					Status.BAD_REQUEST);
		}
	}

	@Path("/{id}")
	@PUT
	@Timed
	public BaseResponse updateOrder(@PathParam("id") LongParam orderId, @NotNull @Valid Order order) {
		final Order updatedOrder = orderRepository.update(orderId.get(), order);

		return new BaseResponse("OK", 204, "Order with ID " + updatedOrder.getId() + " successfully updated.");
	}

	@Path("/{id}")
	@DELETE
	@Timed
	public BaseResponse deleteOrder(@PathParam("id") LongParam orderId) {
		final boolean deleted = orderRepository.deleteById(orderId.get());

		return new BaseResponse(deleted ? "OK" : "FAILED", deleted ? 202 : 400,
				deleted ? "Order with ID " + orderId.get() + " successfully deleted."
						: "Failed to delete order with ID " + orderId.get() + ".");
	}
}