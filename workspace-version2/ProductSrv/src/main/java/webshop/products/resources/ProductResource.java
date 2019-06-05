package webshop.products.resources;

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
import webshop.products.api.BaseResponse;
import webshop.products.api.Product;
import webshop.products.db.ProductRepository;
import webshop.products.messaging.KafkaNotifier;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
	private final long defaultCategoryId;
	private ProductRepository productRepository;
	private Logger log;
	private KafkaNotifier kafkaNotifier;

	public ProductResource(long defaultCategoryId, ProductRepository repository) {
		this.defaultCategoryId = defaultCategoryId;
		this.productRepository = repository;
		this.kafkaNotifier = new KafkaNotifier();
		this.log = LoggerFactory.getLogger(ProductResource.class);
		log.info("ProductResource instantiated...");
	}

	@GET
	@Timed
	public List<Product> getProducts(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<Product> products = productRepository.search(limit.get());

		return products;
	}

	@Path("/{id}")
	@GET
	@Timed
	public Product getProductById(@PathParam("id") LongParam productId) {
		final Product product = productRepository.getById(productId.get());

		if (product == null) {
			final String msg = String.format("Product with ID %d does not exist...", productId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return product;
	}

	@POST
	@Timed
	public BaseResponse createProduct(@NotNull @Valid Product product) {
		if (product.getCategoryId() == 0) {
			product.setCategoryId(defaultCategoryId);
		}
		final Product createdProduct = productRepository.store(product);

		// Publish event that new product has been created
		kafkaNotifier.publishNewProductEvent(createdProduct);

		return new BaseResponse("OK", 201, "Product with ID " + createdProduct.getId() + " successfully created.");
	}

	@Path("/{id}")
	@PUT
	@Timed
	public BaseResponse updateProduct(@PathParam("id") LongParam productId, @NotNull @Valid Product product) {
		final Product updatedProduct = productRepository.update(productId.get(), product);

		return new BaseResponse("OK", 204, "Product with ID " + updatedProduct.getId() + " successfully updated.");
	}

	@Path("/{id}")
	@DELETE
	@Timed
	public BaseResponse deleteProduct(@PathParam("id") LongParam productId) {
		final boolean deleted = productRepository.deleteById(productId.get());

		return new BaseResponse(deleted ? "OK" : "FAILED", deleted ? 202 : 400,
				deleted ? "Product with ID " + productId.get() + " successfully deleted."
						: "Failed to delete product with ID " + productId.get() + ".");
	}
}