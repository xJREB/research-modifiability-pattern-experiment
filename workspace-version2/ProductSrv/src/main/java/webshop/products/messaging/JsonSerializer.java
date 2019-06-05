package webshop.products.messaging;

import java.util.Map;

import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonSerializer<T> implements Serializer<T> {

	private ObjectMapper objectMapper;

	public JsonSerializer() {
	}

	@Override
	public void configure(Map<String, ?> config, boolean isKey) {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	@Override
	public byte[] serialize(String topic, T data) {
		if (data == null) {
			return null;
		}

		try {
			return objectMapper.writeValueAsBytes(data);
		} catch (Exception e) {
			throw new SerializationException("Error serializing JSON message", e);
		}
	}

	@Override
	public void close() {
	}
}
