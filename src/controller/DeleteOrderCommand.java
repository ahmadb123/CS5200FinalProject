package controller;

import java.sql.SQLException;
import model.order.IOrderModel;

/**
 * Admin command that hard-deletes an order. cascades to order_items,
 * payments, shipments, order_logs, and email_notifications via FKs.
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
