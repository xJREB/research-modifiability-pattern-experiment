package webshop.warehouse.db;

import java.util.HashMap;
import java.util.Map;

public class WarehouseRepository {

	private Map<Long, Integer> warehouse;

	public WarehouseRepository() {
		this.warehouse = new HashMap<Long, Integer>() {
			private static final long serialVersionUID = 1L;
			{
				put((long) 1, 5);
				put((long) 2, 4);
				put((long) 3, 3);
				put((long) 4, 2);
				put((long) 5, 1);
			}
		};
	}

	public int getAvailableProductAmount(long productId) {
		int availableAmount = -1;
		if (warehouse.get(productId) != null) {
			availableAmount = warehouse.get(productId);
		}

		return availableAmount;
	}

	public void setAvailableProductAmount(long productId, int amount) {
		warehouse.put(productId, amount);
	}
}
