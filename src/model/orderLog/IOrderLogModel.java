package model.orderLog;

import java.sql.SQLException;
import java.util.List;

/**
 * Contract for the order log model — an append-only audit trail of order events.
 * no update or delete methods; logs are permanent once written. typical use:
 * "order created", "status changed from pending to shipped", "cancelled", etc.
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
