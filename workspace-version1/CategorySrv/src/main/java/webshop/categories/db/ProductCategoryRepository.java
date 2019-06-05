package webshop.categories.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import webshop.categories.api.ProductCategory;

public class ProductCategoryRepository {

	// Categories
	private AtomicLong categoryIdCounter;
	private List<ProductCategory> categories;

	public ProductCategoryRepository() {
		// Categories
		this.categoryIdCounter = new AtomicLong();
		this.categories = new ArrayList<ProductCategory>(Arrays.asList(
				new ProductCategory(categoryIdCounter.incrementAndGet(), "TestCategory1", 0,
						new ArrayList<String>(Arrays.asList("tag1"))),
				new ProductCategory(categoryIdCounter.incrementAndGet(), "TestCategory2", 0,
						new ArrayList<String>(Arrays.asList("tag2"))),
				new ProductCategory(categoryIdCounter.incrementAndGet(), "TestCategory3", 0,
						new ArrayList<String>(Arrays.asList("tag3")))));
	}

	// Product category methods

	public List<ProductCategory> searchCategories(int limit) {

		return categories;
	}

	public ProductCategory getCategoryById(long categoryId) {
		ProductCategory foundCategory = null;

		for (ProductCategory category : categories) {
			if (category.getId() == categoryId) {
				foundCategory = category;
				break;
			}
		}

		return foundCategory;
	}

	public ProductCategory storeCategory(ProductCategory category) {
		final ProductCategory createdCategory = new ProductCategory(categoryIdCounter.incrementAndGet(),
				category.getName(), category.getParentCategoryId(), category.getTags());
		this.categories.add(createdCategory);

		return createdCategory;
	}

	public ProductCategory updateCategory(long categoryId, ProductCategory category) {
		final ProductCategory updatedCategory = category;

		return updatedCategory;
	}

	public boolean deleteCategoryById(long categoryId) {

		return true;
	}

}
