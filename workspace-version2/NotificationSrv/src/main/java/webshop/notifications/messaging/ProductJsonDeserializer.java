package webshop.notifications.messaging;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import webshop.notifications.api.Product;

public class ProductJsonDeserializer implements Deserializer<Product> {

	private ObjectMapper objectMapper;

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> config, boolean isKey) {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

	}

	@Override
	public Product deserialize(String topic, byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		try {
			product = mapper.readValue(data, Product.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return product;
	}

}
