package webshop.categories.resources;

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
import webshop.categories.api.BaseResponse;
import webshop.categories.api.ProductCategory;
import webshop.categories.db.ProductCategoryRepository;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
public class ProductCategoryResource {
	private ProductCategoryRepository categoryRepository;
	private Logger log;

	public ProductCategoryResource(ProductCategoryRepository repository) {
		this.categoryRepository = repository;
		this.log = LoggerFactory.getLogger(ProductCategoryResource.class);
		log.info("ProductCategoryResource instantiated...");
	}

	@GET
	@Timed
	public List<ProductCategory> getCategories(@QueryParam("limit") @DefaultValue("20") IntParam limit) {
		final List<ProductCategory> categories = categoryRepository.search(limit.get());

		return categories;
	}

	@Path("/{id}")
	@GET
	@Timed
	public ProductCategory getCategoryById(@PathParam("id") LongParam categoryId) {
		final ProductCategory category = categoryRepository.getById(categoryId.get());

		if (category == null) {
			final String msg = String.format("Category with ID %d does not exist...", categoryId.get());
			throw new WebApplicationException(msg, Status.NOT_FOUND);
		}

		return category;
	}

	@POST
	@Timed
	public BaseResponse createCategory(@NotNull @Valid ProductCategory category) {
		final ProductCategory createdCategory = categoryRepository.store(category);

		return new BaseResponse("OK", 201, "Category with ID " + createdCategory.getId() + " successfully created.");
	}

	@Path("/{id}")
	@PUT
	@Timed
	public BaseResponse updateCategory(@PathParam("id") LongParam categoryId,
			@NotNull @Valid ProductCategory category) {
		final ProductCategory updatedCategory = categoryRepository.update(categoryId.get(), category);

		return new BaseResponse("OK", 204, "Category with ID " + updatedCategory.getId() + " successfully updated.");
	}

	@Path("/{id}")
	@DELETE
	@Timed
	public BaseResponse deleteCategory(@PathParam("id") LongParam categoryId) {
		final boolean deleted = categoryRepository.deleteById(categoryId.get());

		return new BaseResponse(deleted ? "OK" : "FAILED", deleted ? 202 : 400,
				deleted ? "Category with ID " + categoryId.get() + " successfully deleted."
						: "Failed to delete category with ID " + categoryId.get() + ".");
	}

}