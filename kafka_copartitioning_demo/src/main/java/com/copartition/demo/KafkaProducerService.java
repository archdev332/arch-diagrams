package com.copartition.demo;

import com.copartition.demo.domain.Order;
import com.copartition.demo.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

  private final KafkaTemplate<String, Order> orderKafkaTemplate;
  private final KafkaTemplate<String, Payment> paymentKafkaTemplate;

  @Value("${kafka.topics.orders}")
  private final String ordersTopic;
  @Value("${kafka.topics.payments}")
  private final String paymentsTopic;

  /**
   * Produces an order event to the orders topic.
   * Note that we use customerId as the key to ensure proper partitioning.
   */
  public void produceOrder(Order order) {
    // Using customerId as the key ensures orders from the same customer go to the same partition
    orderKafkaTemplate.send(ordersTopic, order.getCustomerId(), order);
  }

  /**
   * Produces a payment event to the payments topic.
   * Using the same customerId as the key ensures proper copartitioning with orders.
   */
  public void producePayment(Payment payment) {
    // Using customerId as the key ensures payments from the same customer go to the same partition
    paymentKafkaTemplate.send(paymentsTopic, payment.getCustomerId(), payment);
  }
}
