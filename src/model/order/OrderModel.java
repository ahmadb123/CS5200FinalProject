package model.order;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.DBConnection;

/**
 * JDBC implementation of IOrderModel. Routes CRUD and status transitions
 * through stored procedures (place_order, cancel_order, update_order_status,
 * etc.). The calculateOrderTotal method uses a plain PreparedStatement to
 * call the calc_order_total SQL function.
 */
public class OrderModel implements IOrderModel {
  private final DBConnection database;

  /**
   * @param db shared DB connection factory.
   */
  public OrderModel(DBConnection db) {
    this.database = db;
  }

  @Override
  public Order placeOrder(int userId, String shippingMethod, String paymentMethod)
      throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL place_order(?, ?, ?)}")) {

      cs.setInt(1, userId);
      cs.setString(2, shippingMethod);
      cs.setString(3, paymentMethod);

      if (cs.execute()) {
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return mapRow(rs);
          }
        }
      }
    }
    return null;
  }

  @Override
  public Order findOrderById(int orderId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_order_by_id(?)}")) {

      cs.setInt(1, orderId);

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<Order> findOrdersByUser(int userId) throws SQLException {
    List<Order> orders = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_orders_by_user(?)}")) {

      cs.setInt(1, userId);

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          orders.add(mapRow(rs));
        }
      }
    }
    return orders;
  }

  @Override
  public List<Order> findOrdersByStatus(String status) throws SQLException {
    List<Order> orders = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_orders_by_status(?)}")) {

      cs.setString(1, status);

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          orders.add(mapRow(rs));
        }
      }
    }
    return orders;
  }

  @Override
  public List<Order> findAllOrders() throws SQLException {
    List<Order> orders = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_all_orders()}")) {

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          orders.add(mapRow(rs));
        }
      }
    }
    return orders;
  }

  @Override
  public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL update_order_status(?, ?)}")) {

      cs.setInt(1, orderId);
      cs.setString(2, newStatus);

      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean cancelOrder(int orderId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL cancel_order(?)}")) {

      cs.setInt(1, orderId);
      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean deleteOrder(int orderId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL delete_order(?)}")) {

      cs.setInt(1, orderId);
      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public BigDecimal calculateOrderTotal(int orderId) throws SQLException {
    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement("SELECT calc_order_total(?)")) {

      ps.setInt(1, orderId);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          BigDecimal total = rs.getBigDecimal(1);
          return total != null ? total : BigDecimal.ZERO;
        }
      }
    }
    return BigDecimal.ZERO;
  }

  private Order mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("order_date");
    return new Order.Builder()
        .withOrderId(rs.getInt("order_id"))
        .withUserId(rs.getInt("user_id"))
        .withOrderDate(ts != null ? ts.toLocalDateTime() : null)
        .withStatus(rs.getString("status"))
        .withShippingMethod(rs.getString("shipping_method"))
        .withPaymentMethod(rs.getString("payment_method"))
        .withTotalAmount(rs.getBigDecimal("total_amount"))
        .build();
  }
}
