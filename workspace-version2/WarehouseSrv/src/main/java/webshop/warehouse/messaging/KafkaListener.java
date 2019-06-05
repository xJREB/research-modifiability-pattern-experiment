package webshop.warehouse.messaging;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import webshop.warehouse.api.Product;
import webshop.warehouse.resources.WarehouseResource;

public class KafkaListener implements Runnable {
	private final String KAFKA_TOPIC_NAME = "new-products";
	private Properties kafkaProps;
	private Logger log;
	private WarehouseResource warehouseResource;

	public KafkaListener(WarehouseResource warehouseResource) {
		this.warehouseResource = warehouseResource;

		// Set Kafka connection properties
		kafkaProps = new Properties();
		kafkaProps.put("bootstrap.servers", "localhost:9092");
		kafkaProps.put("enable.auto.commit", "true");
		kafkaProps.put("group.id", "WarehouseSrv");
		kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaProps.put("value.deserializer", ProductJsonDeserializer.class);
		this.log = LoggerFactory.getLogger(KafkaListener.class);
		log.info("KafkaNotifier instantiated...");
	}

	@Override
	public void run() {
		@SuppressWarnings("resource")
		final Consumer<String, Product> consumer = new KafkaConsumer<String, Product>(kafkaProps);
		consumer.subscribe(Arrays.asList(KAFKA_TOPIC_NAME));
		while (true) {
			ConsumerRecords<String, Product> messages = consumer.poll(1000);
			for (ConsumerRecord<String, Product> message : messages) {
				Product createdProduct = message.value();

				// Stock up on 10 copies of the new product
				LongParam productId = new LongParam(String.valueOf(createdProduct.getId()));
				IntParam amount = new IntParam("10");
				this.warehouseResource.updateProductAvailability(productId, amount);
			}
		}
	}
}
