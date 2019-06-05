package webshop.warehouse.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import webshop.warehouse.api.BaseResponse;
import webshop.warehouse.api.ProductAvailabilityCheckResponse;
import webshop.warehouse.db.WarehouseRepository;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WarehouseResource {

	private WarehouseRepository warehouseRepository;
	private Logger log;

	public WarehouseResource(WarehouseRepository repository) {
		this.warehouseRepository = repository;
		this.log = LoggerFactory.getLogger(WarehouseResource.class);
		log.info("WarehouseResource instantiated...");
	}

	// Warehouse resources

	@Path("/products/{id}/availability")
	@GET
	@Timed
	public ProductAvailabilityCheckResponse checkProductAvailability(@PathParam("id") LongParam productId,
			@QueryParam("amount") @DefaultValue("1") IntParam requestedAmount) {

		log.info("Checking availability for product with ID " + productId.get() + " for the amount of "
				+ requestedAmount.get() + "...");
		final int availableAmount = warehouseRepository.getAvailableProductAmount(productId.get());

		if (availableAmount == -1) {
			final String msg = String.format("Product with ID %d does not exist...", productId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		final int MINIMAL_REMAINING_AMOUNT_NECESSARY = 2;
		return new ProductAvailabilityCheckResponse(productId.get(),
				(availableAmount - requestedAmount.get() >= MINIMAL_REMAINING_AMOUNT_NECESSARY), requestedAmount.get());
	}

	@Path("/products/{id}/availability")
	@PUT
	@Timed
	public BaseResponse updateProductAvailability(@PathParam("id") LongParam productId,
			@QueryParam("amount") @DefaultValue("1") IntParam amount) {

		log.info("Setting available amount for product with ID " + productId.get() + " to " + amount.get() + "...");
		warehouseRepository.setAvailableProductAmount(productId.get(), amount.get());

		return new BaseResponse("OK", 200,
				"Available amount for product with ID " + productId.get() + " successfully updated.");
	}

}