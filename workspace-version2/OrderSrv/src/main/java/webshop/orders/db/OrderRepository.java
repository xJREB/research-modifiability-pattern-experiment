package webshop.orders.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import webshop.orders.api.Order;
import webshop.orders.api.OrderItem;

public class OrderRepository {

	private AtomicLong orderIdCounter;
	private List<Order> orders;

	public OrderRepository() {
		this.orderIdCounter = new AtomicLong();
		this.orders = new ArrayList<Order>(Arrays.asList(
				new Order(orderIdCounter.incrementAndGet(), 1, Arrays.asList(new OrderItem(1, 1), new OrderItem(2, 1))),
				new Order(orderIdCounter.incrementAndGet(), 1, Arrays.asList(new OrderItem(2, 1), new OrderItem(3, 2))),
				new Order(orderIdCounter.incrementAndGet(), 2, Arrays.asList(new OrderItem(2, 2))),
				new Order(orderIdCounter.incrementAndGet(), 2, Arrays.asList(new OrderItem(1, 1), new OrderItem(3, 2))),
				new Order(orderIdCounter.incrementAndGet(), 3, Arrays.asList(new OrderItem(2, 1)))));
	}

	public List<Order> search(int limit) {
		if (limit > 0) {
			return orders.subList(0, Math.min(orders.size(), limit));
		}
		return orders;
	}

	public Order getById(long orderId) {
		Order foundOrder = null;

		for (Order order : orders) {
			if (order.getId() == orderId) {
				foundOrder = order;
				break;
			}
		}

		return foundOrder;
	}

	public Order store(Order order) {
		final Order createdOrder = new Order(orderIdCounter.incrementAndGet(), order.getCustomerId(), order.getItems());
		this.orders.add(createdOrder);

		return createdOrder;
	}

	public Order update(long orderId, Order order) {
		final Order updatedOrder = order;

		return updatedOrder;
	}

	public boolean deleteById(long orderId) {
		return true;
	}

}
