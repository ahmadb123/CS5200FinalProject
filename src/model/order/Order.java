package model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order entity. Immutable data holder for one row in the orders table.
 * Fields: orderId, userId (foreign key to users), orderDate, status,
 * shippingMethod, paymentMethod, totalAmount. Instances are created
 * through the nested Builder. toBuilder() returns a clone you can edit
 * in place to change a single field without touching the original.
 */
public class Order {
  private final int orderId;
  private final int userId;
  private final LocalDateTime orderDate;
  private final String status;
  private final String shippingMethod;
  private final String paymentMethod;
  private final BigDecimal totalAmount;

  private Order(int orderId, int userId, LocalDateTime orderDate, String status,
                String shippingMethod, String paymentMethod, BigDecimal totalAmount) {
    this.orderId = orderId;
    this.userId = userId;
    this.orderDate = orderDate;
    this.status = status;
    this.shippingMethod = shippingMethod;
    this.paymentMethod = paymentMethod;
    this.totalAmount = totalAmount;
  }

  public int getOrderId() {
    return orderId;
  }

  public int getUserId() {
    return userId;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public String getStatus() {
    return status;
  }

  public String getShippingMethod() {
    return shippingMethod;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public Builder toBuilder() {
    return new Builder()
        .withOrderId(this.orderId)
        .withUserId(this.userId)
        .withOrderDate(this.orderDate)
        .withStatus(this.status)
        .withShippingMethod(this.shippingMethod)
        .withPaymentMethod(this.paymentMethod)
        .withTotalAmount(this.totalAmount);
  }

  public static class Builder {
    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private String status;
    private String shippingMethod;
    private String paymentMethod;
    private BigDecimal totalAmount;

    public Builder withOrderId(int orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder withUserId(int userId) {
      this.userId = userId;
      return this;
    }

    public Builder withOrderDate(LocalDateTime orderDate) {
      this.orderDate = orderDate;
      return this;
    }

    public Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    public Builder withShippingMethod(String shippingMethod) {
      this.shippingMethod = shippingMethod;
      return this;
    }

    public Builder withPaymentMethod(String paymentMethod) {
      this.paymentMethod = paymentMethod;
      return this;
    }

    public Builder withTotalAmount(BigDecimal totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Order build() {
      return new Order(orderId, userId, orderDate, status,
                       shippingMethod, paymentMethod, totalAmount);
    }
  }
}
