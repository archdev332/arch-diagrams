package com.copartition.demo.domain;

import java.math.BigDecimal;

public class OrderPaymentStatus {

  private String orderId;
  private String customerId;
  private BigDecimal orderAmount;
  private String paymentId;
  private BigDecimal paymentAmount;
  private OrderStatus status;
  private PaymentMethod paymentMethod;

  OrderPaymentStatus(String orderId, String customerId, BigDecimal orderAmount, String paymentId, BigDecimal paymentAmount, OrderStatus status, PaymentMethod paymentMethod) {
    this.orderId = orderId;
    this.customerId = customerId;
    this.orderAmount = orderAmount;
    this.paymentId = paymentId;
    this.paymentAmount = paymentAmount;
    this.status = status;
    this.paymentMethod = paymentMethod;
  }

  public static OrderPaymentStatusBuilder builder() {
    return new OrderPaymentStatusBuilder();
  }

  public String getOrderId() {
    return this.orderId;
  }

  public String getCustomerId() {
    return this.customerId;
  }

  public BigDecimal getOrderAmount() {
    return this.orderAmount;
  }

  public String getPaymentId() {
    return this.paymentId;
  }

  public BigDecimal getPaymentAmount() {
    return this.paymentAmount;
  }

  public OrderStatus getStatus() {
    return this.status;
  }

  public PaymentMethod getPaymentMethod() {
    return this.paymentMethod;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public void setOrderAmount(BigDecimal orderAmount) {
    this.orderAmount = orderAmount;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public void setPaymentAmount(BigDecimal paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof OrderPaymentStatus)) return false;
    final OrderPaymentStatus other = (OrderPaymentStatus) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$orderId = this.getOrderId();
    final Object other$orderId = other.getOrderId();
    if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
    final Object this$customerId = this.getCustomerId();
    final Object other$customerId = other.getCustomerId();
    if (this$customerId == null ? other$customerId != null : !this$customerId.equals(other$customerId))
      return false;
    final Object this$orderAmount = this.getOrderAmount();
    final Object other$orderAmount = other.getOrderAmount();
    if (this$orderAmount == null ? other$orderAmount != null : !this$orderAmount.equals(other$orderAmount))
      return false;
    final Object this$paymentId = this.getPaymentId();
    final Object other$paymentId = other.getPaymentId();
    if (this$paymentId == null ? other$paymentId != null : !this$paymentId.equals(other$paymentId)) return false;
    final Object this$paymentAmount = this.getPaymentAmount();
    final Object other$paymentAmount = other.getPaymentAmount();
    if (this$paymentAmount == null ? other$paymentAmount != null : !this$paymentAmount.equals(other$paymentAmount))
      return false;
    final Object this$status = this.getStatus();
    final Object other$status = other.getStatus();
    if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
    final Object this$paymentMethod = this.getPaymentMethod();
    final Object other$paymentMethod = other.getPaymentMethod();
    if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod))
      return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof OrderPaymentStatus;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $orderId = this.getOrderId();
    result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
    final Object $customerId = this.getCustomerId();
    result = result * PRIME + ($customerId == null ? 43 : $customerId.hashCode());
    final Object $orderAmount = this.getOrderAmount();
    result = result * PRIME + ($orderAmount == null ? 43 : $orderAmount.hashCode());
    final Object $paymentId = this.getPaymentId();
    result = result * PRIME + ($paymentId == null ? 43 : $paymentId.hashCode());
    final Object $paymentAmount = this.getPaymentAmount();
    result = result * PRIME + ($paymentAmount == null ? 43 : $paymentAmount.hashCode());
    final Object $status = this.getStatus();
    result = result * PRIME + ($status == null ? 43 : $status.hashCode());
    final Object $paymentMethod = this.getPaymentMethod();
    result = result * PRIME + ($paymentMethod == null ? 43 : $paymentMethod.hashCode());
    return result;
  }

  public String toString() {
    return "OrderPaymentStatus(orderId=" + this.getOrderId() + ", customerId=" + this.getCustomerId() + ", orderAmount=" + this.getOrderAmount() + ", paymentId=" + this.getPaymentId() + ", paymentAmount=" + this.getPaymentAmount() + ", status=" + this.getStatus() + ", paymentMethod=" + this.getPaymentMethod() + ")";
  }

  public static class OrderPaymentStatusBuilder {
    private String orderId;
    private String customerId;
    private BigDecimal orderAmount;
    private String paymentId;
    private BigDecimal paymentAmount;
    private OrderStatus status;
    private PaymentMethod paymentMethod;

    OrderPaymentStatusBuilder() {
    }

    public OrderPaymentStatusBuilder orderId(String orderId) {
      this.orderId = orderId;
      return this;
    }

    public OrderPaymentStatusBuilder customerId(String customerId) {
      this.customerId = customerId;
      return this;
    }

    public OrderPaymentStatusBuilder orderAmount(BigDecimal orderAmount) {
      this.orderAmount = orderAmount;
      return this;
    }

    public OrderPaymentStatusBuilder paymentId(String paymentId) {
      this.paymentId = paymentId;
      return this;
    }

    public OrderPaymentStatusBuilder paymentAmount(BigDecimal paymentAmount) {
      this.paymentAmount = paymentAmount;
      return this;
    }

    public OrderPaymentStatusBuilder status(OrderStatus status) {
      this.status = status;
      return this;
    }

    public OrderPaymentStatusBuilder paymentMethod(PaymentMethod paymentMethod) {
      this.paymentMethod = paymentMethod;
      return this;
    }

    public OrderPaymentStatus build() {
      return new OrderPaymentStatus(orderId, customerId, orderAmount, paymentId, paymentAmount, status, paymentMethod);
    }

    public String toString() {
      return "OrderPaymentStatus.OrderPaymentStatusBuilder(orderId=" + this.orderId + ", customerId=" + this.customerId + ", orderAmount=" + this.orderAmount + ", paymentId=" + this.paymentId + ", paymentAmount=" + this.paymentAmount + ", status=" + this.status + ", paymentMethod=" + this.paymentMethod + ")";
    }
  }
}
