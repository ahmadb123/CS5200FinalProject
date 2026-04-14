package model.payment;

import java.sql.SQLException;
import java.util.List;

/**
 * Payment model interface. Each order has at most one payment (enforced by
 * a UNIQUE constraint on order_id), so findPaymentByOrderId returns a
 * single payment instead of a list. A payment starts as 'pending' and
 * moves to 'paid', 'failed', or 'refunded'.
 */
public interface IPaymentModel {

  /**
   * creates a new payment row with status 'pending' and paid_at null.
   *
   * @param orderId order this payment is for.
   * @param method  payment method label (e.g. "credit", "paypal").
   * @return the new payment with its DB-assigned id.
   * @throws SQLException on DB error.
   */
  Payment createPayment(int orderId, String method) throws SQLException;

  /**
   * @param paymentId payment to look up.
   * @return the matching payment, or null.
   * @throws SQLException on DB error.
   */
  Payment findPaymentById(int paymentId) throws SQLException;

  /**
   * 1:1 lookup - since every order has at most one payment.
   *
   * @param orderId order to look up.
   * @return that order's payment, or null.
   * @throws SQLException on DB error.
   */
  Payment findPaymentByOrderId(int orderId) throws SQLException;

  /**
   * @return every payment in the system.
   * @throws SQLException on DB error.
   */
  List<Payment> findAllPayments() throws SQLException;

  /**
   * convenience - sets status to 'paid' and stamps paid_at with NOW().
   *
   * @param paymentId payment to mark paid.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean markAsPaid(int paymentId) throws SQLException;

  /**
   * generic status update for 'failed' / 'refunded' / etc.
   *
   * @param paymentId payment to update.
   * @param status    new status value.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean updatePaymentStatus(int paymentId, String status) throws SQLException;

  /**
   * @param paymentId payment to delete.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deletePayment(int paymentId) throws SQLException;
}
