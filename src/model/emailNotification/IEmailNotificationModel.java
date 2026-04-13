package model.emailNotification;

import java.sql.SQLException;
import java.util.List;

/**
 * Contract for the email notification model. notifications are append-only
 * records of messages sent to users about their orders (confirmations, status
 * updates, cancellations). status ("sent", "failed", "pending") can be updated
 * but notifications are never deleted — they serve as an audit trail.
 */
public interface IEmailNotificationModel {

  /**
   * records that a notification was sent. stamps sent_at = NOW() and status 'sent'.
   *
   * @param orderId order the notification relates to.
   * @param email   recipient email address.
   * @param subject notification subject / summary.
   * @return the persisted notification with its DB-assigned id.
   * @throws SQLException on DB error.
   */
  EmailNotification sendNotification(int orderId, String email, String subject) throws SQLException;

  /**
   * @param notificationId notification to look up.
   * @return the matching notification, or null.
   * @throws SQLException on DB error.
   */
  EmailNotification findNotificationById(int notificationId) throws SQLException;

  /**
   * returns all notifications for an order (user's message history).
   *
   * @param orderId order to look up.
   * @return notifications for that order, newest first.
   * @throws SQLException on DB error.
   */
  List<EmailNotification> findNotificationsByOrder(int orderId) throws SQLException;

  /**
   * @return every notification in the system.
   * @throws SQLException on DB error.
   */
  List<EmailNotification> findAllNotifications() throws SQLException;

  /**
   * updates the delivery status of a notification (e.g. mark as 'failed').
   *
   * @param notificationId notification to update.
   * @param status         new status value.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean updateStatus(int notificationId, String status) throws SQLException;
}
