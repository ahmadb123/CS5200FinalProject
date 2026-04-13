package model.payment;

import java.sql.SQLException;
import java.util.List;

public interface IPaymentModel {
  Payment createPayment(int orderId, String method) throws SQLException;

  Payment findPaymentById(int paymentId) throws SQLException;

  Payment findPaymentByOrderId(int orderId) throws SQLException;

  List<Payment> findAllPayments() throws SQLException;

  boolean markAsPaid(int paymentId) throws SQLException;

  boolean updatePaymentStatus(int paymentId, String status) throws SQLException;

  boolean deletePayment(int paymentId) throws SQLException;
}