package controller;

import java.sql.SQLException;
import model.order.IOrderModel;

/**
 * Command that sets an order's status to 'cancelled'. execute() returns
 * true if a row was updated.
 */
public class CancelOrderCommand implements Icommand<Boolean> {

  private final IOrderModel orderModel;
  private final int orderId;

  public CancelOrderCommand(IOrderModel orderModel, int orderId) {
    this.orderModel = orderModel;
    this.orderId = orderId;
  }

  @Override
  public Boolean execute() throws SQLException {
    return orderModel.cancelOrder(orderId);
  }
}
