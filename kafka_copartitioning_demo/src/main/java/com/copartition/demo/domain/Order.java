package com.copartition.demo.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Order {

  private String orderId;
  private String customerId; // Used as the key for partitioning
  private BigDecimal amount;
  private OrderStatus status;
}
