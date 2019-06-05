package webshop.customers.resources;

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
import webshop.customers.api.BaseResponse;
import webshop.customers.api.CreditRatingCheckResponse;
import webshop.customers.api.Customer;
import webshop.customers.db.CustomerRepository;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {
	private final int defaultCreditRating;
	private CustomerRepository customerRepository;
	private Logger log;

	public CustomerResource(int defaultCreditRating, CustomerRepository repository) {
		this.defaultCreditRating = defaultCreditRating;
		this.customerRepository = repository;
		this.log = LoggerFactory.getLogger(CustomerResource.class);
		log.info("CustomerResource instantiated...");
	}

	@GET
	@Timed
	public List<Customer> getCustomers(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<Customer> customers = customerRepository.search(limit.get());

		return customers;
	}

	@Path("/{customerId}")
	@GET
	@Timed
	public Customer getCustomerById(@PathParam("customerId") LongParam customerId) {
		final Customer customer = customerRepository.getById(customerId.get());

		if (customer == null) {
			final String msg = String.format("Customer with ID %d does not exist...", customerId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return customer;
	}

	@POST
	@Timed
	public BaseResponse createCustomer(@NotNull @Valid Customer customer) {
		if (customer.getCreditRating() == 0) {
			customer.setCreditRating(this.defaultCreditRating);
		}
		final Customer createdCustomer = customerRepository.store(customer);

		return new BaseResponse("OK", 201, "Customer with ID " + createdCustomer.getId() + " successfully created.");
	}

	@Path("/{customerId}")
	@PUT
	@Timed
	public BaseResponse updateCustomer(@PathParam("customerId") LongParam customerId, @NotNull @Valid Customer customer) {
		final Customer updatedCustomer = customerRepository.update(customerId.get(), customer);

		return new BaseResponse("OK", 204, "Customer with ID " + updatedCustomer.getId() + " successfully updated.");
	}

	@Path("/{customerId}")
	@DELETE
	@Timed
	public BaseResponse deleteCustomer(@PathParam("customerId") LongParam customerId) {
		final boolean deleted = customerRepository.deleteById(customerId.get());

		return new BaseResponse(deleted ? "OK" : "FAILED", deleted ? 202 : 400,
				deleted ? "Customer with ID " + customerId.get() + " successfully deleted."
						: "Failed to delete customer with ID " + customerId.get() + ".");
	}

	@Path("/{customerId}/credit-rating-check")
	@GET
	@Timed
	public CreditRatingCheckResponse updateAndCheckCreditRating(@PathParam("customerId") LongParam customerId) {
		log.info("Revalidating credit rating for customer with ID " + customerId.get() + "...");

		final int rating = customerRepository.updateAndGetRating(customerId.get());

		if (rating == -1) {
			final String msg = String.format("Customer with ID %d does not exist...", customerId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		// 1 --> best rating
		// 6 --> worst rating
		return new CreditRatingCheckResponse(customerId.get(), rating);
	}
}