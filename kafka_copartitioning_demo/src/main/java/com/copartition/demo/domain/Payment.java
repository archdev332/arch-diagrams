package com.copartition.demo.domain;

import java.math.BigDecimal;

public class Payment {

  private String paymentId;
  private String customerId; // Used as the key for partitioning
  private String orderId;    // Used to match with specific orders
  private BigDecimal amount;
  private PaymentMethod method;

  public Payment() {
  }

  public String getPaymentId() {
    return this.paymentId;
  }

  public String getCustomerId() {
    return this.customerId;
  }

  public String getOrderId() {
    return this.orderId;
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public PaymentMethod getMethod() {
    return this.method;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public void setMethod(PaymentMethod method) {
    this.method = method;
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Payment)) return false;
    final Payment other = (Payment) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$paymentId = this.getPaymentId();
    final Object other$paymentId = other.getPaymentId();
    if (this$paymentId == null ? other$paymentId != null : !this$paymentId.equals(other$paymentId)) return false;
    final Object this$customerId = this.getCustomerId();
    final Object other$customerId = other.getCustomerId();
    if (this$customerId == null ? other$customerId != null : !this$customerId.equals(other$customerId))
      return false;
    final Object this$orderId = this.getOrderId();
    final Object other$orderId = other.getOrderId();
    if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
    final Object this$amount = this.getAmount();
    final Object other$amount = other.getAmount();
    if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount)) return false;
    final Object this$method = this.getMethod();
    final Object other$method = other.getMethod();
    if (this$method == null ? other$method != null : !this$method.equals(other$method)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Payment;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $paymentId = this.getPaymentId();
    result = result * PRIME + ($paymentId == null ? 43 : $paymentId.hashCode());
    final Object $customerId = this.getCustomerId();
    result = result * PRIME + ($customerId == null ? 43 : $customerId.hashCode());
    final Object $orderId = this.getOrderId();
    result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
    final Object $amount = this.getAmount();
    result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
    final Object $method = this.getMethod();
    result = result * PRIME + ($method == null ? 43 : $method.hashCode());
    return result;
  }

  public String toString() {
    return "Payment(paymentId=" + this.getPaymentId() + ", customerId=" + this.getCustomerId() + ", orderId=" + this.getOrderId() + ", amount=" + this.getAmount() + ", method=" + this.getMethod() + ")";
  }
}
