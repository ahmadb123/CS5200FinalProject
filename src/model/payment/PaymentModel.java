package model.payment;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.DBConnection;

/**
 * Concrete JDBC implementation of IPaymentModel. all operations go through
 * stored procedures (create_payment, mark_payment_paid, update_payment_status,
 * etc.). the 1:1 lookup by order uses find_payment_by_order_id. paid_at is
 * nullable — handled in mapRow with a null check on the Timestamp.
 */
public class PaymentModel implements IPaymentModel {
  private final DBConnection database;

  /**
   * @param db shared DB connection factory.
   */
  public PaymentModel(DBConnection db) {
    this.database = db;
  }

  @Override
  public Payment createPayment(int orderId, String method) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL create_payment(?, ?)}")) {

      cs.setInt(1, orderId);
      cs.setString(2, method);

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
  public Payment findPaymentById(int paymentId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_payment_by_id(?)}")) {

      cs.setInt(1, paymentId);

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    }
    return null;
  }

  @Override
  public Payment findPaymentByOrderId(int orderId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_payment_by_order_id(?)}")) {

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
  public List<Payment> findAllPayments() throws SQLException {
    List<Payment> payments = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_all_payments()}")) {

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          payments.add(mapRow(rs));
        }
      }
    }
    return payments;
  }

  @Override
  public boolean markAsPaid(int paymentId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL mark_payment_paid(?)}")) {

      cs.setInt(1, paymentId);
      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean updatePaymentStatus(int paymentId, String status) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL update_payment_status(?, ?)}")) {

      cs.setInt(1, paymentId);
      cs.setString(2, status);

      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean deletePayment(int paymentId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL delete_payment(?)}")) {

      cs.setInt(1, paymentId);
      return cs.executeUpdate() > 0;
    }
  }

  private Payment mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("paid_at");
    return new Payment.Builder()
        .withPaymentId(rs.getInt("payment_id"))
        .withOrderId(rs.getInt("order_id"))
        .withPaymentMethod(rs.getString("payment_method"))
        .withPaymentStatus(rs.getString("payment_status"))
        .withPaidAt(ts != null ? ts.toLocalDateTime() : null)
        .build();
  }
}
