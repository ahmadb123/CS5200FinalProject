package model.order.orderItem;

import java.math.BigDecimal;

/**
 * Order item entity. One row in the order_items table - a product and
 * quantity attached to an order. The composite primary key is orderId
 * plus productId; the remaining fields are quantity, unitPrice, and
 * subtotal. Subtotal is filled in by a DB trigger, not by Java code.
 * Immutable; built through the nested Builder.
 */
public class OrderItem {
  private final int orderId;
  private final int productId;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final BigDecimal subtotal;

  private OrderItem(int orderId, int productId, int quantity,
                    BigDecimal unitPrice, BigDecimal subtotal) {
    this.orderId = orderId;
    this.productId = productId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.subtotal = subtotal;
  }

  public int getOrderId() {
    return orderId;
  }

  public int getProductId() {
    return productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public Builder toBuilder() {
    return new Builder()
        .withOrderId(this.orderId)
        .withProductId(this.productId)
        .withQuantity(this.quantity)
        .withUnitPrice(this.unitPrice)
        .withSubtotal(this.subtotal);
  }

  public static class Builder {
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public Builder withOrderId(int orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder withProductId(int productId) {
      this.productId = productId;
      return this;
    }

    public Builder withQuantity(int quantity) {
      this.quantity = quantity;
      return this;
    }

    public Builder withUnitPrice(BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
      return this;
    }

    public Builder withSubtotal(BigDecimal subtotal) {
      this.subtotal = subtotal;
      return this;
    }

    public OrderItem build() {
      return new OrderItem(orderId, productId, quantity, unitPrice, subtotal);
    }
  }
}
