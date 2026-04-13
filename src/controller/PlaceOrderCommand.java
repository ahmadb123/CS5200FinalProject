package controller;

import java.sql.SQLException;
import model.order.IOrderModel;
import model.order.Order;

/**
 * Command that creates a new order for a user with the given shipping and
 * payment method. execute() returns the new order with its DB-assigned id.
 */
public class PlaceOrderCommand implements Icommand<Order> {

  private final IOrderModel orderModel;
  private final int userId;
  private final String shippingMethod;
  private final String paymentMethod;

  public PlaceOrderCommand(IOrderModel orderModel, int userId,
                           String shippingMethod, String paymentMethod) {
    this.orderModel = orderModel;
    this.userId = userId;
    this.shippingMethod = shippingMethod;
    this.paymentMethod = paymentMethod;
  }

  @Override
  public Order execute() throws SQLException {
    return orderModel.placeOrder(userId, shippingMethod, paymentMethod);
  }
}
