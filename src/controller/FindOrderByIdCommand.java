package controller;

import java.sql.SQLException;
import model.order.IOrderModel;
import model.order.Order;

/**
 * Command that loads one order by id. execute() returns the matching order
 * or null if not found.
 */
public class FindOrderByIdCommand implements Icommand<Order> {

  private final IOrderModel orderModel;
  private final int orderId;

  public FindOrderByIdCommand(IOrderModel orderModel, int orderId) {
    this.orderModel = orderModel;
    this.orderId = orderId;
  }

  @Override
  public Order execute() throws SQLException {
    return orderModel.findOrderById(orderId);
  }
}
