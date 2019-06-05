package webshop.customers.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import webshop.customers.api.Customer;

public class CustomerRepository {

	private AtomicLong customerIdCounter;
	private List<Customer> customers;

	public CustomerRepository() {
		this.customerIdCounter = new AtomicLong();

		this.customers = new ArrayList<Customer>(Arrays.asList(
				new Customer(customerIdCounter.incrementAndGet(), "TestCustomer1", "customer1@test.com", 6),
				new Customer(customerIdCounter.incrementAndGet(), "TestCustomer2", "customer2@test.com", 5),
				new Customer(customerIdCounter.incrementAndGet(), "TestCustomer3", "customer3@test.com", 4),
				new Customer(customerIdCounter.incrementAndGet(), "TestCustomer4", "customer4@test.com", 3),
				new Customer(customerIdCounter.incrementAndGet(), "TestCustomer5", "customer5@test.com", 2),
				new Customer(customerIdCounter.incrementAndGet(), "TestCustomer6", "customer6@test.com", 1)));
	}

	public List<Customer> search(int limit) {
		if (limit > 0) {
			return customers.subList(0, Math.min(customers.size(), limit));
		}

		return customers;
	}

	public Customer getById(long customerId) {
		Customer foundCustomer = null;

		for (Customer customer : customers) {
			if (customer.getId() == customerId) {
				foundCustomer = customer;
				break;
			}
		}

		return foundCustomer;
	}

	public Customer store(Customer customer) {
		final Customer createdCustomer = new Customer(customerIdCounter.incrementAndGet(), customer.getName(),
				customer.getEmail(), customer.getCreditRating());
		this.customers.add(createdCustomer);

		return createdCustomer;
	}

	public Customer update(long customerId, Customer customer) {
		final Customer updatedCustomer = customer;

		return updatedCustomer;
	}

	public boolean deleteById(long customerId) {

		return true;
	}

	public int updateAndGetRating(long customerId) {
		int creditRating = -1;

		for (Customer customer : customers) {
			if (customer.getId() == customerId) {
				creditRating = customer.getCreditRating();
				break;
			}
		}

		return creditRating;
	}

}
