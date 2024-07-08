package io.buzzy.common.messaging.model.producer;

import io.buzzy.common.messaging.model.MessageDTO;
import io.buzzy.common.util.JsonUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

public abstract class BuzzyKafkaProducer<T extends MessageDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuzzyKafkaProducer.class);

    protected final String topic;
    protected final KafkaTemplate<String, T> producer;

    protected BuzzyKafkaProducer(String topic, KafkaTemplate<String, T> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    public void send(T message) {
        LOGGER.debug("Sending message to topic: {} value: {}", topic, JsonUtil.toJson(message));
        ProducerRecord<String, T> producerRecord = new ProducerRecord<>(topic, message);
        producer.send(producerRecord).whenComplete(this::handleSendResult);
    }

    private void handleSendResult(SendResult<String, T> result, Throwable throwable) {
        if (throwable == null) {
            LOGGER.debug("Message sent successfully with offset: {}", result.getRecordMetadata().offset());
        } else {
            LOGGER.error("Sending message to topic: {} resulted to: {}", topic, throwable.getMessage(), throwable);
        }
    }
}
