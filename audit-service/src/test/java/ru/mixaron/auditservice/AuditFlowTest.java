package ru.mixaron.auditservice;

import com.example.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import ru.mixaron.auditservice.repository.AuditRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class AuditFlowTest extends AuditServiceIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AuditRepository auditRepository;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
    }

    private TransactionEvent createTransactionEvent(long eventId, long userId) {
        return  TransactionEvent.newBuilder()
                .setId(eventId)
                .setUserId(userId)
                .setAmount(100.0)
                .setCurrency("USD")
                .setTimestamp(Instant.now())
                .build();
    }

    @Test
    void shouldSaveTransactionToDatabase() {
        TransactionEvent event = createTransactionEvent(1L, 1L);

        kafkaTemplate.send("transaction-events", event);
        await()
                .pollInterval(Duration.ofSeconds(1))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var records = auditRepository.findAll();
                    assertEquals(1, records.size());
                    assertEquals(1L, records.getFirst().getUserId());
                });
    }

    @Test
    void shouldSendToDltWhenMessageIsInvalid() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-dlt-group", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

        Consumer<String, byte[]> dltConsumer = new DefaultKafkaConsumerFactory<String, byte[]>(consumerProps)
                .createConsumer();

        dltConsumer.subscribe(List.of("transaction-events.audit.DLT"));

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(kafka.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (Producer<String, String> badProducer = new DefaultKafkaProducerFactory<String, String>(producerProps).createProducer()) {
            badProducer.send(new ProducerRecord<>("transaction-events", "key", "invalid message"));
        }

        ConsumerRecord<String, byte[]> record = KafkaTestUtils.getSingleRecord(dltConsumer, "transaction-events.audit.DLT");

        assertNotNull(record);

        dltConsumer.close();

        assertEquals(0, auditRepository.count());
    }

    @Test
    void shouldSaveOnceWhenSendDuplicates() {
        TransactionEvent event = createTransactionEvent(1L, 1L);

        kafkaTemplate.send("transaction-events", event);
        kafkaTemplate.send("transaction-events", event);

        await()
                .pollInterval(Duration.ofSeconds(1))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var records = auditRepository.findAll();
                    assertEquals(1, records.size());
                    assertEquals(1L, records.getFirst().getUserId());
                });
    }
}
