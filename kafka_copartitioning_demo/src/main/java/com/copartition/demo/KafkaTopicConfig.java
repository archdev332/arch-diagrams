package com.copartition.demo;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicConfig {

  @Value("${partition-count:6}")
  private int partitionCount;
  @Value("${replication-factor:1}")
  private int replicationFactor;
  @Value("${orders:orders}")
  private String ordersTopic;
  @Value("${payments:payments}")
  private String paymentsTopic;
  @Value("${order-payment-status:order-payment-status}")
  private String orderPaymentStatusTopic;

  @Bean
  public NewTopic ordersTopic() {
    return TopicBuilder.name(ordersTopic)
        .partitions(partitionCount)
        .replicas(replicationFactor)
        .build();
  }

  @Bean
  public NewTopic paymentsTopic() {
    return TopicBuilder.name(paymentsTopic)
        .partitions(partitionCount)
        .replicas(replicationFactor)
        .build();
  }

  @Bean
  public NewTopic orderPaymentStatusTopic() {
    return TopicBuilder.name(orderPaymentStatusTopic)
        .partitions(partitionCount)
        .replicas(replicationFactor)
        .build();
  }
}
