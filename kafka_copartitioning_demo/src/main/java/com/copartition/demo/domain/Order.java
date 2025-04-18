package com.copartition.demo.domain;

import java.math.BigDecimal;

public class Order {

  private String orderId;
  private String customerId; // Used as the key for partitioning
  private BigDecimal amount;
  private OrderStatus status;

  public Order() {
  }

  public String getOrderId() {
    return this.orderId;
  }

  public String getCustomerId() {
    return this.customerId;
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public OrderStatus getStatus() {
    return this.status;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Order)) return false;
    final Order other = (Order) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$orderId = this.getOrderId();
    final Object other$orderId = other.getOrderId();
    if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
    final Object this$customerId = this.getCustomerId();
    final Object other$customerId = other.getCustomerId();
    if (this$customerId == null ? other$customerId != null : !this$customerId.equals(other$customerId))
      return false;
    final Object this$amount = this.getAmount();
    final Object other$amount = other.getAmount();
    if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount)) return false;
    final Object this$status = this.getStatus();
    final Object other$status = other.getStatus();
    if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Order;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $orderId = this.getOrderId();
    result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
    final Object $customerId = this.getCustomerId();
    result = result * PRIME + ($customerId == null ? 43 : $customerId.hashCode());
    final Object $amount = this.getAmount();
    result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
    final Object $status = this.getStatus();
    result = result * PRIME + ($status == null ? 43 : $status.hashCode());
    return result;
  }

  public String toString() {
    return "Order(orderId=" + this.getOrderId() + ", customerId=" + this.getCustomerId() + ", amount=" + this.getAmount() + ", status=" + this.getStatus() + ")";
  }
}
