package ru.mixaron.transactionservice;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class TransactionFlowTest extends TransactionServiceIntegrationTest {

    @Test
    void ShouldSendMessageToKafka() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-transaction-group", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

        Consumer<String, byte[]> dltConsumer = new DefaultKafkaConsumerFactory<String, byte[]>(consumerProps)
                .createConsumer();

        dltConsumer.subscribe(List.of("transaction-events"));

        ConsumerRecord<String, byte[]> record = KafkaTestUtils.getSingleRecord(dltConsumer, "transaction-events");

        await()
                .pollInterval(Duration.ofSeconds(1))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> assertNotNull(record));
    }
}
