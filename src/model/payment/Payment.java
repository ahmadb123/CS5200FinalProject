package model.payment;

import java.time.LocalDateTime;

/**
 * Payment entity. A row in the payments table, one per order (the
 * orderId column has a UNIQUE constraint). Holds paymentId, orderId,
 * paymentMethod, paymentStatus, and paidAt. The paidAt timestamp is
 * nullable and only gets set once the payment moves from 'pending' to
 * 'paid'. Instances are immutable and built through the nested Builder.
 */
public class Payment {
  private final int paymentId;
  private final int orderId;
  private final String paymentMethod;
  private final String paymentStatus;
  private final LocalDateTime paidAt;

  private Payment(int paymentId, int orderId, String paymentMethod,
                  String paymentStatus, LocalDateTime paidAt) {
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.paymentMethod = paymentMethod;
    this.paymentStatus = paymentStatus;
    this.paidAt = paidAt;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public int getOrderId() {
    return orderId;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public String getPaymentStatus() {
    return paymentStatus;
  }

  public LocalDateTime getPaidAt() {
    return paidAt;
  }

  public Builder toBuilder() {
    return new Builder()
        .withPaymentId(this.paymentId)
        .withOrderId(this.orderId)
        .withPaymentMethod(this.paymentMethod)
        .withPaymentStatus(this.paymentStatus)
        .withPaidAt(this.paidAt);
  }

  public static class Builder {
    private int paymentId;
    private int orderId;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paidAt;

    public Builder withPaymentId(int paymentId) {
      this.paymentId = paymentId;
      return this;
    }

    public Builder withOrderId(int orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder withPaymentMethod(String paymentMethod) {
      this.paymentMethod = paymentMethod;
      return this;
    }

    public Builder withPaymentStatus(String paymentStatus) {
      this.paymentStatus = paymentStatus;
      return this;
    }

    public Builder withPaidAt(LocalDateTime paidAt) {
      this.paidAt = paidAt;
      return this;
    }

    public Payment build() {
      return new Payment(paymentId, orderId, paymentMethod, paymentStatus, paidAt);
    }
  }
}
