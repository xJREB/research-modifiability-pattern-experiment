package webshop.products.messaging;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webshop.products.api.Product;

public class KafkaNotifier {

	private static final String KAFKA_TOPIC_NAME = "new-products";
	private Properties kafkaProps;
	private Logger log;

	public KafkaNotifier() {
		// Messaging related properties
		this.kafkaProps = new Properties();
		kafkaProps.put("bootstrap.servers", "localhost:9092");
		kafkaProps.put("acks", "all");
		kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		kafkaProps.put("value.serializer", JsonSerializer.class);
		this.log = LoggerFactory.getLogger(KafkaNotifier.class);
		log.info("KafkaNotifier instantiated...");
	}

	public void publishNewProductEvent(Product newProduct) {
		Producer<String, Product> producer = new KafkaProducer<String, Product>(kafkaProps);
		try {
			producer.send(new ProducerRecord<String, Product>(KAFKA_TOPIC_NAME, "NEW_PRODUCT_EVENT", newProduct));
			producer.close();
			log.info("'Product Created' event successfully dispatched!");
		} catch (Exception e) {
			log.error("'Product Created' event could not be dispatched!");
			log.error(e.toString());
		}
	}

}
