package controller;

import java.sql.SQLException;
import model.order.IOrderModel;
import model.order.Order;

/**
 * Fetches a single order by its id. execute() returns the matching
 * order, or null if no row was found.
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
