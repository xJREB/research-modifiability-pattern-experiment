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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import webshop.orders.api.BaseResponse;
import webshop.orders.api.Order;
import webshop.orders.db.OrderRepository;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
	private OrderRepository orderRepository;
	private Logger log;

	public OrderResource(OrderRepository repository) {
		this.orderRepository = repository;
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
	public Order createOrder(@NotNull @Valid Order order) {
		final Order createdOrder = orderRepository.store(order);
		log.info("Order with ID " + createdOrder.getId() + " successfully created.");

		return createdOrder;
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