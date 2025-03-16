package com.cdc.demo;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.cdc.demo.event.UserEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.consumer.max.poll.interval.ms:100}")
  private int maxPollIntervalMs;

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, UserEvent> userEventFactory(
      KafkaProperties kafkaProperties, ObjectMapper objectMapper) {
    Map<String, Object> consumerConfig = setupConsumerConfig(kafkaProperties);
    ConcurrentKafkaListenerContainerFactory<String, UserEvent> listenerFactory =
        new ConcurrentKafkaListenerContainerFactory<>();
    listenerFactory.setAutoStartup(true);
    listenerFactory.setConsumerFactory(
        new DefaultKafkaConsumerFactory<>(consumerConfig, new StringDeserializer(),
            new JsonDeserializer<>(UserEvent.class, objectMapper, false)));
    listenerFactory.setBatchListener(true);
    return listenerFactory;
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(IGNORE_UNKNOWN, true);
    objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  private Map<String, Object> setupConsumerConfig(KafkaProperties kafkaProperties) {
    Map<String, Object> consumerConfig = new HashMap<>(5);
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
    consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
    consumerConfig.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
    consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getConsumer().getMaxPollRecords());
    return consumerConfig;
  }
}
