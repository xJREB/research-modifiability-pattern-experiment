package webshop.products.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import webshop.products.api.Product;

public class ProductRepository {

	// Products
	private AtomicLong productIdCounter;
	private List<Product> products;

	public ProductRepository() {
		// Products
		this.productIdCounter = new AtomicLong();
		this.products = new ArrayList<Product>(
				Arrays.asList(new Product(productIdCounter.incrementAndGet(), "TestProduct1", 1, 12.5),
						new Product(productIdCounter.incrementAndGet(), "TestProduct2", 1, 13),
						new Product(productIdCounter.incrementAndGet(), "TestProduct3", 2, 15),
						new Product(productIdCounter.incrementAndGet(), "TestProduct4", 2, 3.99),
						new Product(productIdCounter.incrementAndGet(), "TestProduct5", 3, 7.20)));
	}

	// Product methods

	public List<Product> searchProducts(int limit) {
		return products;
	}

	public Product getProductById(long productId) {
		Product foundProduct = null;

		for (Product product : products) {
			if (product.getId() == productId) {
				foundProduct = product;
				break;
			}
		}

		return foundProduct;
	}

	public Product storeProduct(Product product) {
		final Product createdProduct = new Product(productIdCounter.incrementAndGet(), product.getName(),
				product.getCategoryId(), product.getPrice());
		this.products.add(createdProduct);

		return createdProduct;
	}

	public Product updateProduct(long productId, Product product) {
		final Product updatedProduct = product;

		return updatedProduct;
	}

	public boolean deleteProductById(long productId) {

		return true;
	}
}
