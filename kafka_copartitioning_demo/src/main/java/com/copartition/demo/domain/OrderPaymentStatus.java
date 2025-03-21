package com.copartition.demo.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Builder
@ToString
public class OrderPaymentStatus {

  private String orderId;
  private String customerId;
  private BigDecimal orderAmount;
  private String paymentId;
  private BigDecimal paymentAmount;
  private OrderStatus status;
  private PaymentMethod paymentMethod;
}
