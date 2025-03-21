package com.copartition.demo.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Payment {

  private String paymentId;
  private String customerId; // Used as the key for partitioning
  private String orderId;    // Used to match with specific orders
  private BigDecimal amount;
  private PaymentMethod method;
}
