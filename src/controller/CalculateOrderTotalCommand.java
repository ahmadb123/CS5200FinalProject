package controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import model.order.IOrderModel;

/**
 * Computes an order's total by calling the calc_order_total SQL
 * function. execute() returns the total, or 0 if the order has no
 * items.
 */
public class CalculateOrderTotalCommand implements Icommand<BigDecimal> {

  private final IOrderModel orderModel;
  private final int orderId;

  public CalculateOrderTotalCommand(IOrderModel orderModel, int orderId) {
    this.orderModel = orderModel;
    this.orderId = orderId;
  }

  @Override
  public BigDecimal execute() throws SQLException {
    return orderModel.calculateOrderTotal(orderId);
  }
}
