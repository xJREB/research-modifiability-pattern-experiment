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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import webshop.products.api.BaseResponse;
import webshop.products.api.Product;
import webshop.products.api.ProductAvailabilityCheckResponse;
import webshop.products.api.ProductCategory;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ProductFacadeResource {
	private Client restClient;
	private Logger log;
	private final String PRODUCT_SRV_ENDPOINT = "http://localhost:8050";
	private final String CATEGORY_SRV_ENDPOINT = "http://localhost:8060";
	private final String WAREHOUSE_SRV_ENDPOINT = "http://localhost:8070";

	public ProductFacadeResource(Client restClient) {
		this.restClient = restClient;
		this.log = LoggerFactory.getLogger(ProductFacadeResource.class);
		this.log.info("ProductSrvFacade started...");
	}

	// Product resources

	@Path("/products")
	@GET
	@Timed
	public List<Product> getProducts(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final Invocation.Builder request = restClient.target(PRODUCT_SRV_ENDPOINT + "/products?limit=" + limit)
				.request();
		return request.get(new GenericType<List<Product>>() {
		});

	}

	@Path("/products/{id}")
	@GET
	@Timed
	public Product getProductById(@PathParam("id") LongParam productId) {
		final Invocation.Builder request = restClient.target(PRODUCT_SRV_ENDPOINT + "/products/" + productId).request();
		return request.get(Product.class);
	}

	@Path("/products")
	@POST
	@Timed
	public BaseResponse createProduct(@NotNull @Valid Product product) {
		final Invocation.Builder request = restClient.target(PRODUCT_SRV_ENDPOINT + "/products").request();
		return request.post(Entity.json(product), BaseResponse.class);
	}

	@Path("/products/{id}")
	@PUT
	@Timed
	public BaseResponse updateProduct(@PathParam("id") LongParam productId, @NotNull @Valid Product product) {
		final Invocation.Builder request = restClient.target(PRODUCT_SRV_ENDPOINT + "/products/" + productId).request();
		return request.put(Entity.json(product), BaseResponse.class);
	}

	@Path("/products/{id}")
	@DELETE
	@Timed
	public BaseResponse deleteProduct(@PathParam("id") LongParam productId) {
		final Invocation.Builder request = restClient.target(PRODUCT_SRV_ENDPOINT + "/products/" + productId).request();
		return request.delete(BaseResponse.class);
	}

	// Warehouse resources

	@Path("/products/{id}/availability")
	@GET
	@Timed
	public ProductAvailabilityCheckResponse checkProductAvailability(@PathParam("id") LongParam productId,
			@QueryParam("amount") @DefaultValue("1") IntParam requestedAmount) {
		final Invocation.Builder request = restClient
				.target(WAREHOUSE_SRV_ENDPOINT + "/products/" + productId + "/availability?amount=" + requestedAmount)
				.request();
		return request.get(ProductAvailabilityCheckResponse.class);
	}

	@Path("/products/{id}/availability")
	@PUT
	@Timed
	public BaseResponse updateProductAvailability(@PathParam("id") LongParam productId,
			@QueryParam("amount") @DefaultValue("1") IntParam amount) {
		final Invocation.Builder request = restClient
				.target(WAREHOUSE_SRV_ENDPOINT + "/products/" + productId + "/availability?amount=" + amount).request();
		return request.put(Entity.json(""), BaseResponse.class);
	}

	// Product category resources

	@Path("/categories")
	@GET
	@Timed
	public List<ProductCategory> getCategories(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final Invocation.Builder request = restClient.target(CATEGORY_SRV_ENDPOINT + "/categories?limit=" + limit)
				.request();
		return request.get(new GenericType<List<ProductCategory>>() {
		});
	}

	@Path("/categories/{id}")
	@GET
	@Timed
	public ProductCategory getCategoryById(@PathParam("id") LongParam categoryId) {
		final Invocation.Builder request = restClient.target(CATEGORY_SRV_ENDPOINT + "/categories/" + categoryId)
				.request();
		return request.get(ProductCategory.class);
	}

	@Path("/categories")
	@POST
	@Timed
	public BaseResponse createCategory(@NotNull @Valid ProductCategory category) {
		final Invocation.Builder request = restClient.target(CATEGORY_SRV_ENDPOINT + "/categories").request();
		return request.post(Entity.json(category), BaseResponse.class);
	}

	@Path("/categories/{id}")
	@PUT
	@Timed
	public BaseResponse updateCategory(@PathParam("id") LongParam categoryId,
			@NotNull @Valid ProductCategory category) {
		final Invocation.Builder request = restClient.target(CATEGORY_SRV_ENDPOINT + "/categories/" + categoryId)
				.request();
		return request.put(Entity.json(category), BaseResponse.class);
	}

	@Path("/categories/{id}")
	@DELETE
	@Timed
	public BaseResponse deleteCategory(@PathParam("id") LongParam categoryId) {
		final Invocation.Builder request = restClient.target(CATEGORY_SRV_ENDPOINT + "/categories/" + categoryId)
				.request();
		return request.delete(BaseResponse.class);
	}
}