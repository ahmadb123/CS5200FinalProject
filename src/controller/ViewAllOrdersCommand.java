package controller;

import java.sql.SQLException;
import java.util.List;
import model.order.IOrderModel;
import model.order.Order;

/**
 * Admin command that loads every order in the system. execute() returns
 * the full list.
 */
public class ViewAllOrdersCommand implements Icommand<List<Order>> {

  private final IOrderModel orderModel;

  public ViewAllOrdersCommand(IOrderModel orderModel) {
    this.orderModel = orderModel;
  }

  @Override
  public List<Order> execute() throws SQLException {
    return orderModel.findAllOrders();
  }
}
