package model.order.orderItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IOrderItemModel {

  OrderItem addItem(int orderId, int productId, int quantity, BigDecimal unitPrice)
      throws SQLException;

  OrderItem findItem(int orderId, int productId) throws SQLException;

  List<OrderItem> findItemsByOrder(int orderId) throws SQLException;

  boolean updateQuantity(int orderId, int productId, int newQuantity) throws SQLException;

  boolean removeItem(int orderId, int productId) throws SQLException;

  boolean clearOrder(int orderId) throws SQLException;
}
