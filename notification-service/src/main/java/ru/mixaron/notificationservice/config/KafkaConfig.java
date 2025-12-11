package ru.mixaron.notificationservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaProperties properties) {
        Map<String, Object> producerProps = new HashMap<>(properties.buildProducerProperties(null));

        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        DeadLetterPublishingRecoverer recoverer = getDeadLetterPublishingRecoverer(producerProps);

        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0));
    }

    private static DeadLetterPublishingRecoverer getDeadLetterPublishingRecoverer(Map<String, Object> producerProps) {
        DefaultKafkaProducerFactory<Object, Object> dltProducerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<Object, Object> dltTemplate = new KafkaTemplate<>(dltProducerFactory);

        return new DeadLetterPublishingRecoverer(dltTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".notification.DLT", record.partition()));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            CommonErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
