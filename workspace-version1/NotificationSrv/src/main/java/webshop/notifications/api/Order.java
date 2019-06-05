package webshop.notifications.api;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
	private long id;
	private Date created;
	private String status;
	@Min(1)
	private long customerId;
	@NotNull
	private List<OrderItem> items;

	public Order() {
		// Jackson deserialization
	}

	public Order(long id, long customerId, List<OrderItem> items) {
		this.id = id;
		this.customerId = customerId;
		this.items = items;
		this.created = new Date();
		this.status = "NEW";
	}

	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public Date getCreated() {
		return created;
	}

	@JsonProperty
	public String getStatus() {
		return status;
	}

	@JsonProperty
	public long getCustomerId() {
		return customerId;
	}

	@JsonProperty
	public List<OrderItem> getItems() {
		return items;
	}
}