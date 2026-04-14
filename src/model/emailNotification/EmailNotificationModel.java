package model.emailNotification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import model.AbstractJdbcModel;
import model.DBConnection;

/**
 * IEmailNotificationModel implementation built on AbstractJdbcModel with
 * inline SQL constants. Behaves almost like the order log table:
 * notifications are append-only except for the delivery status column,
 * which can be updated after the initial insert (for example to switch
 * it from "sent" to "failed").
 */
public class EmailNotificationModel extends AbstractJdbcModel implements IEmailNotificationModel {

  private static final String SQL_INSERT =
      "INSERT INTO email_notifications (order_id, email, subject, sent_at, status) "
          + "VALUES (?, ?, ?, NOW(), 'sent')";

  private static final String SQL_SELECT_BY_ID =
      "SELECT notification_id, order_id, email, subject, sent_at, status "
          + "FROM email_notifications WHERE notification_id = ?";

  private static final String SQL_SELECT_BY_ORDER =
      "SELECT notification_id, order_id, email, subject, sent_at, status "
          + "FROM email_notifications WHERE order_id = ? ORDER BY sent_at DESC";

  private static final String SQL_SELECT_ALL =
      "SELECT notification_id, order_id, email, subject, sent_at, status "
          + "FROM email_notifications ORDER BY sent_at DESC";

  private static final String SQL_UPDATE_STATUS =
      "UPDATE email_notifications SET status = ? WHERE notification_id = ?";

  public EmailNotificationModel(DBConnection db) {
    super(db);
  }

  @Override
  public EmailNotification sendNotification(int orderId, String email, String subject)
      throws SQLException {
    int newId = executeInsertReturningKey(SQL_INSERT, ps -> {
      ps.setInt(1, orderId);
      ps.setString(2, email);
      ps.setString(3, subject);
    });
    return newId > 0 ? findNotificationById(newId) : null;
  }

  @Override
  public EmailNotification findNotificationById(int notificationId) throws SQLException {
    return queryOne(SQL_SELECT_BY_ID,
        ps -> ps.setInt(1, notificationId),
        this::mapRow);
  }

  @Override
  public List<EmailNotification> findNotificationsByOrder(int orderId) throws SQLException {
    return queryMany(SQL_SELECT_BY_ORDER,
        ps -> ps.setInt(1, orderId),
        this::mapRow);
  }

  @Override
  public List<EmailNotification> findAllNotifications() throws SQLException {
    return queryMany(SQL_SELECT_ALL, ps -> {}, this::mapRow);
  }

  @Override
  public boolean updateStatus(int notificationId, String status) throws SQLException {
    return executeUpdate(SQL_UPDATE_STATUS, ps -> {
      ps.setString(1, status);
      ps.setInt(2, notificationId);
    }) > 0;
  }

  private EmailNotification mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("sent_at");
    return new EmailNotification.Builder()
        .withNotificationId(rs.getInt("notification_id"))
        .withOrderId(rs.getInt("order_id"))
        .withEmail(rs.getString("email"))
        .withSubject(rs.getString("subject"))
        .withSentAt(ts != null ? ts.toLocalDateTime() : null)
        .withStatus(rs.getString("status"))
        .build();
  }
}
