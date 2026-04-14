package model.order;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Order model interface. Placing orders, looking them up by user or
 * status, status transitions (cancel / update), deletion, and the
 * SQL-function-backed total calculation. Orders have a foreign key to
 * users but this interface takes plain int ids so the model layer is not
 * coupled to any user type.
 */
public interface IOrderModel {

  /**
   * creates a new order for the given user with initial status 'pending'
   * and total 0. items are added separately via OrderItemModel.
   *
   * @param userId         owner of the order.
   * @param shippingMethod shipping label (e.g. "standard", "express").
   * @param paymentMethod  payment label (e.g. "credit", "paypal").
   * @return the newly created order with its DB-assigned id.
   * @throws SQLException on DB error.
   */
  Order placeOrder(int userId, String shippingMethod, String paymentMethod) throws SQLException;

  /**
   * @param orderId order to look up.
   * @return matching order, or null.
   * @throws SQLException on DB error.
   */
  Order findOrderById(int orderId) throws SQLException;

  /**
   * loads one user's order history, newest first.
   *
   * @param userId owner of the orders.
   * @return list of that user's orders.
   * @throws SQLException on DB error.
   */
  List<Order> findOrdersByUser(int userId) throws SQLException;

  /**
   * admin filter - find orders by their status string.
   *
   * @param status status value (e.g. "pending", "shipped").
   * @return matching orders.
   * @throws SQLException on DB error.
   */
  List<Order> findOrdersByStatus(String status) throws SQLException;

  /**
   * @return every order in the system.
   * @throws SQLException on DB error.
   */
  List<Order> findAllOrders() throws SQLException;

  /**
   * updates an order's status to the given value.
   *
   * @param orderId   order to update.
   * @param newStatus new status value.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean updateOrderStatus(int orderId, String newStatus) throws SQLException;

  /**
   * convenience - sets status to 'cancelled'.
   *
   * @param orderId order to cancel.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean cancelOrder(int orderId) throws SQLException;

  /**
   * hard-deletes an order. cascades to order_items, payments, shipments,
   * order_logs, and email_notifications.
   *
   * @param orderId order to delete.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deleteOrder(int orderId) throws SQLException;

  /**
   * Calls the calc_order_total SQL function to compute the total for an
   * order from its items.
   *
   * @param orderId order whose total to compute.
   * @return the computed total, or 0 if the order has no items.
   * @throws SQLException on DB error.
   */
  BigDecimal calculateOrderTotal(int orderId) throws SQLException;
}
