package controller.uiFeatures;

/**
 * Order-related actions the UI can call: placing orders, reading orders
 * (by user, by id, or all), cancelling or updating an order's status,
 * deleting an order, and computing an order total through the
 * calc_order_total SQL function.
 */
public interface OrderFeatures {

  /**
   * creates a new order for the given user with shipping + payment method.
   *
   * @param userId   user placing the order.
   * @param shipping shipping method label (e.g. "standard", "express").
   * @param payment  payment method label (e.g. "credit", "paypal").
   */
  void placeOrder(int userId, String shipping, String payment);

  /**
   * loads every order belonging to one user and pushes the list to the view.
   *
   * @param userId owner of the orders to find.
   */
  void findOrdersByUser(int userId);

  /**
   * admin action. loads every order in the system.
   */
  void findAllOrders();

  /**
   * loads one order by its id and pushes the detail view.
   *
   * @param orderId order to look up.
   */
  void findOrderById(int orderId);

  /**
   * sets an order's status to 'cancelled'.
   *
   * @param orderId order to cancel.
   */
  void cancelOrder(int orderId);

  /**
   * admin action. updates an order's status string.
   *
   * @param orderId   order to update.
   * @param newStatus new status value (e.g. "shipped", "delivered").
   */
  void updateOrdersStatus(int orderId, String newStatus);

  /**
   * admin action. hard-deletes an order (cascades to items, payment, shipment).
   *
   * @param orderId order to delete.
   */
  void deleteOrder(int orderId);

  /**
   * calls the calc_order_total SQL function for an order and pushes the result.
   *
   * @param orderId order whose total to compute.
   */
  void calculateOrderTotal(int orderId);
}
