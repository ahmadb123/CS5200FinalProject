package controller;

import java.sql.SQLException;
import model.order.IOrderModel;

/**
 * Admin-only: changes an order's status string. execute() returns true
 * when a row was updated.
 */
public class UpdateOrderStatusCommand implements Icommand<Boolean> {

  private final IOrderModel orderModel;
  private final int orderId;
  private final String newStatus;

  public UpdateOrderStatusCommand(IOrderModel orderModel, int orderId, String newStatus) {
    this.orderModel = orderModel;
    this.orderId = orderId;
    this.newStatus = newStatus;
  }

  @Override
  public Boolean execute() throws SQLException {
    return orderModel.updateOrderStatus(orderId, newStatus);
  }
}
