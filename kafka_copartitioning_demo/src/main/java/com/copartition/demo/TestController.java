package com.copartition.demo;

import com.copartition.demo.domain.Order;
import com.copartition.demo.domain.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

  private final KafkaProducerService producerService;

  @PostMapping("/orders")
  public Map<String, String> createOrder(@RequestBody Order order) throws JsonProcessingException {
    producerService.produceOrder(order);
    return Map.of("Result", "Order produced with ID: " + order.getOrderId() + " for customer: " + order.getCustomerId());
  }

  @PostMapping("/payments")
  public Map<String, String> createPayment(@RequestBody Payment payment) throws JsonProcessingException {
    producerService.producePayment(payment);
    return Map.of("Result", "Payment produced with ID: " + payment.getPaymentId() + " for customer: " + payment.getCustomerId());
  }
}
