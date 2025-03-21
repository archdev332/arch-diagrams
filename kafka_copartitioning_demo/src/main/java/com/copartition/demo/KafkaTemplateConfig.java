package com.copartition.demo;

import com.copartition.demo.domain.Order;
import com.copartition.demo.domain.Payment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaTemplateConfig {

  @Bean
  public KafkaTemplate<String, Order> orderKafkaTemplate(ProducerFactory<String, Order> pf) {
    return new KafkaTemplate<>(pf);
  }

  @Bean
  public KafkaTemplate<String, Payment> paymentKafkaTemplate(ProducerFactory<String, Payment> pf) {
    return new KafkaTemplate<>(pf);
  }
}
