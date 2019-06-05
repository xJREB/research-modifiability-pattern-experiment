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
import webshop.products.api.BaseResponse;
import webshop.products.api.NewProductMailRequest;
import webshop.products.api.Product;
import webshop.products.db.ProductRepository;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
	private Client restClient;
	private final long defaultCategoryId;
	private ProductRepository productRepository;
	private Logger log;

	public ProductResource(Client restClient, long defaultCategoryId, ProductRepository repository) {
		this.restClient = restClient;
		this.defaultCategoryId = defaultCategoryId;
		this.productRepository = repository;
		this.log = LoggerFactory.getLogger(ProductResource.class);
		log.info("ProductResource instantiated...");
	}

	// Product resources

	@Path("/products")
	@GET
	@Timed
	public List<Product> getProducts(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<Product> products = productRepository.searchProducts(limit.get());

		return products;
	}

	@Path("/products/{id}")
	@GET
	@Timed
	public Product getProductById(@PathParam("id") LongParam productId) {
		final Product product = productRepository.getProductById(productId.get());

		if (product == null) {
			final String msg = String.format("Product with ID %d does not exist...", productId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return product;
	}

	@Path("/products")
	@POST
	@Timed
	public BaseResponse createProduct(@NotNull @Valid Product product) {
		if (product.getCategoryId() == 0) {
			product.setCategoryId(defaultCategoryId);
		}
		final Product createdProduct = productRepository.storeProduct(product);

		// Adding the new product to NotificationSrv DB
		Invocation.Builder request = restClient.target("http://localhost:8010/new-products").request();
		request.post(Entity.json(createdProduct), BaseResponse.class);

		// Creating a new product mail request for the NotificationSrv
		NewProductMailRequest newProductMailRequest = new NewProductMailRequest("NEW_PRODUCT_MAIL", createdProduct);
		request = restClient.target("http://localhost:8010/product-mails").request();
		request.post(Entity.json(newProductMailRequest), BaseResponse.class);

		// Stock up on 10 copies of the new product
		request = restClient
				.target("http://localhost:8070/products/" + createdProduct.getId() + "/availability?amount=10")
				.request();
		request.put(Entity.json(""), BaseResponse.class);

		return new BaseResponse("OK", 201, "Product with ID " + createdProduct.getId() + " successfully created.");
	}

	@Path("/products/{id}")
	@PUT
	@Timed
	public BaseResponse updateProduct(@PathParam("id") LongParam productId, @NotNull @Valid Product product) {
		final Product updatedProduct = productRepository.updateProduct(productId.get(), product);

		return new BaseResponse("OK", 204, "Product with ID " + updatedProduct.getId() + " successfully updated.");
	}

	@Path("/products/{id}")
	@DELETE
	@Timed
	public BaseResponse deleteProduct(@PathParam("id") LongParam productId) {
		final boolean deleted = productRepository.deleteProductById(productId.get());

		return new BaseResponse(deleted ? "OK" : "FAILED", deleted ? 202 : 400,
				deleted ? "Product with ID " + productId.get() + " successfully deleted."
						: "Failed to delete product with ID " + productId.get() + ".");
	}
}