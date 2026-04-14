package controller;

import java.sql.SQLException;
import model.order.IOrderModel;

/**
 * Admin-only: permanently deletes an order. Foreign keys cascade the
 * delete through order_items, payments, shipments, order_logs, and
 * email_notifications.
 */
public class DeleteOrderCommand implements Icommand<Boolean> {

  private final IOrderModel orderModel;
  private final int orderId;

  public DeleteOrderCommand(IOrderModel orderModel, int orderId) {
    this.orderModel = orderModel;
    this.orderId = orderId;
  }

  @Override
  public Boolean execute() throws SQLException {
    return orderModel.deleteOrder(orderId);
  }
}
