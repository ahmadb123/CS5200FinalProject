package controller;

import java.sql.SQLException;
import java.util.List;
import model.order.IOrderModel;
import model.order.Order;

/**
 * Command that loads one user's order history.
 * execute() returns the list (possibly empty).
 */
public class ViewMyOrdersCommand implements Icommand<List<Order>> {

  private final IOrderModel orderModel;
  private final int userId;

  public ViewMyOrdersCommand(IOrderModel orderModel, int userId) {
    this.orderModel = orderModel;
    this.userId = userId;
  }

  @Override
  public List<Order> execute() throws SQLException {
    return orderModel.findOrdersByUser(userId);
  }
}
