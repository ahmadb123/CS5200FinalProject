package model.order;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IOrderModel {

  Order placeOrder(int userId, String shippingMethod, String paymentMethod) throws SQLException;

  Order findOrderById(int orderId) throws SQLException;

  List<Order> findOrdersByUser(int userId) throws SQLException;

  List<Order> findOrdersByStatus(String status) throws SQLException;

  List<Order> findAllOrders() throws SQLException;

  boolean updateOrderStatus(int orderId, String newStatus) throws SQLException;

  boolean cancelOrder(int orderId) throws SQLException;

  boolean deleteOrder(int orderId) throws SQLException;

  BigDecimal calculateOrderTotal(int orderId) throws SQLException;
}
