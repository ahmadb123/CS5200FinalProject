package model.order.orderItem;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DBConnection;
import model.order.orderItem.IOrderItemModel;

public class OrderItemModel implements IOrderItemModel {
  private final DBConnection database;

  public OrderItemModel(DBConnection db) {
    this.database = db;
  }

  @Override
  public OrderItem addItem(int orderId, int productId, int quantity, BigDecimal unitPrice)
      throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL add_order_item(?, ?, ?, ?)}")) {

      cs.setInt(1, orderId);
      cs.setInt(2, productId);
      cs.setInt(3, quantity);
      cs.setBigDecimal(4, unitPrice);

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
  public OrderItem findItem(int orderId, int productId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_order_item(?, ?)}")) {

      cs.setInt(1, orderId);
      cs.setInt(2, productId);

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<OrderItem> findItemsByOrder(int orderId) throws SQLException {
    List<OrderItem> items = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_items_by_order(?)}")) {

      cs.setInt(1, orderId);

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          items.add(mapRow(rs));
        }
      }
    }
    return items;
  }

  @Override
  public boolean updateQuantity(int orderId, int productId, int newQuantity) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL update_item_quantity(?, ?, ?)}")) {

      cs.setInt(1, orderId);
      cs.setInt(2, productId);
      cs.setInt(3, newQuantity);

      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean removeItem(int orderId, int productId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL remove_order_item(?, ?)}")) {

      cs.setInt(1, orderId);
      cs.setInt(2, productId);

      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean clearOrder(int orderId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL clear_order_items(?)}")) {

      cs.setInt(1, orderId);
      return cs.executeUpdate() > 0;
    }
  }

  private OrderItem mapRow(ResultSet rs) throws SQLException {
    return new OrderItem.Builder()
        .withOrderId(rs.getInt("order_id"))
        .withProductId(rs.getInt("product_id"))
        .withQuantity(rs.getInt("quantity"))
        .withUnitPrice(rs.getBigDecimal("unit_price"))
        .withSubtotal(rs.getBigDecimal("subtotal"))
        .build();
  }
}
