package model.orderLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import model.AbstractJdbcModel;
import model.DBConnection;

/**
 * IOrderLogModel implementation. Extends AbstractJdbcModel and uses
 * inline SQL string constants. The table is append-only by design, so
 * there are only insert and select queries here - audit log rows are
 * never updated or deleted.
 */
public class OrderLogModel extends AbstractJdbcModel implements IOrderLogModel {

  private static final String SQL_INSERT =
      "INSERT INTO order_logs (order_id, action_type, action_description, created_at) "
          + "VALUES (?, ?, ?, NOW())";

  private static final String SQL_SELECT_BY_ID =
      "SELECT log_id, order_id, action_type, action_description, created_at "
          + "FROM order_logs WHERE log_id = ?";

  private static final String SQL_SELECT_BY_ORDER =
      "SELECT log_id, order_id, action_type, action_description, created_at "
          + "FROM order_logs WHERE order_id = ? ORDER BY created_at ASC";

  private static final String SQL_SELECT_RECENT =
      "SELECT log_id, order_id, action_type, action_description, created_at "
          + "FROM order_logs ORDER BY created_at DESC LIMIT ?";

  public OrderLogModel(DBConnection db) {
    super(db);
  }

  @Override
  public OrderLog addLog(int orderId, String actionType, String description) throws SQLException {
    int newId = executeInsertReturningKey(SQL_INSERT, ps -> {
      ps.setInt(1, orderId);
      ps.setString(2, actionType);
      ps.setString(3, description);
    });
    return newId > 0 ? findLogById(newId) : null;
  }

  @Override
  public OrderLog findLogById(int logId) throws SQLException {
    return queryOne(SQL_SELECT_BY_ID,
        ps -> ps.setInt(1, logId),
        this::mapRow);
  }

  @Override
  public List<OrderLog> findLogsByOrder(int orderId) throws SQLException {
    return queryMany(SQL_SELECT_BY_ORDER,
        ps -> ps.setInt(1, orderId),
        this::mapRow);
  }

  @Override
  public List<OrderLog> findRecentLogs(int limit) throws SQLException {
    return queryMany(SQL_SELECT_RECENT,
        ps -> ps.setInt(1, limit),
        this::mapRow);
  }

  private OrderLog mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("created_at");
    return new OrderLog.Builder()
        .withLogId(rs.getInt("log_id"))
        .withOrderId(rs.getInt("order_id"))
        .withActionType(rs.getString("action_type"))
        .withActionDescription(rs.getString("action_description"))
        .withCreatedAt(ts != null ? ts.toLocalDateTime() : null)
        .build();
  }
}
