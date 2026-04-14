package model.orderLog;

import java.sql.SQLException;
import java.util.List;

/**
 * Order log model interface. An append-only audit trail of order events.
 * There are no update or delete methods - once a log row is written it is
 * permanent. Example entries: "order created", "status changed from
 * pending to shipped", "cancelled".
 */
public interface IOrderLogModel {

  /**
   * appends a new log entry for an order.
   *
   * @param orderId     order being logged.
   * @param actionType  short action tag (e.g. "STATUS_CHANGE", "CANCELLED").
   * @param description human-readable description.
   * @return the persisted log with its DB-assigned id and timestamp.
   * @throws SQLException on DB error.
   */
  OrderLog addLog(int orderId, String actionType, String description) throws SQLException;

  /**
   * @param logId log entry to look up.
   * @return the matching log, or null.
   * @throws SQLException on DB error.
   */
  OrderLog findLogById(int logId) throws SQLException;

  /**
   * returns all logs for an order in chronological order.
   *
   * @param orderId order whose logs to fetch.
   * @return the order's audit trail.
   * @throws SQLException on DB error.
   */
  List<OrderLog> findLogsByOrder(int orderId) throws SQLException;

  /**
   * returns the most recent N log entries across all orders (admin dashboard).
   *
   * @param limit max number of logs to return.
   * @return recent logs, newest first.
   * @throws SQLException on DB error.
   */
  List<OrderLog> findRecentLogs(int limit) throws SQLException;
}
