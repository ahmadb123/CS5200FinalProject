package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared base class for JDBC-backed model implementations. Pulls out the
 * try-with-resources, parameter setter, and row-mapping boilerplate that
 * would otherwise repeat in every DAO method. Subclasses call queryOne,
 * queryMany, executeUpdate, or executeInsertReturningKey with an SQL
 * string plus lambdas; this base class manages the connection, statement,
 * and result set. Used by ShipmentModel, OrderLogModel,
 * EmailNotificationModel, and ExchangeRateModel. The older entity models
 * (User, Product, Order, OrderItem, Payment) were written before this
 * base class and still call CallableStatement directly.
 */
public abstract class AbstractJdbcModel {

  protected final DBConnection database;

  /**
   * @param database shared DB connection factory.
   */
  protected AbstractJdbcModel(DBConnection database) {
    this.database = database;
  }

  /**
   * Parameter setter lambda type. Binds the placeholders in a prepared
   * statement to their actual values.
   */
  @FunctionalInterface
  public interface StatementSetter {
    /**
     * binds parameters on the given prepared statement.
     *
     * @param ps prepared statement to configure.
     * @throws SQLException if binding fails.
     */
    void set(PreparedStatement ps) throws SQLException;
  }

  /**
   * Row mapper lambda type. Builds a typed object from a result set row.
   *
   * @param <T> entity type produced.
   */
  @FunctionalInterface
  public interface RowMapper<T> {
    /**
     * maps the current row of the result set to a typed object.
     *
     * @param rs result set positioned on the row.
     * @return the mapped object.
     * @throws SQLException if reading columns fails.
     */
    T map(ResultSet rs) throws SQLException;
  }

  /**
   * runs a SELECT expected to return at most one row.
   *
   * @param sql    query string with ? placeholders.
   * @param setter binds parameters.
   * @param mapper maps the row to an entity.
   * @param <T>    entity type.
   * @return the mapped entity, or null if no row matched.
   * @throws SQLException on DB error.
   */
  protected <T> T queryOne(String sql, StatementSetter setter, RowMapper<T> mapper) throws SQLException {
    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      setter.set(ps);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return mapper.map(rs);
        }
      }
    }
    return null;
  }

  /**
   * runs a SELECT expected to return multiple rows.
   *
   * @param sql    query string with ? placeholders.
   * @param setter binds parameters.
   * @param mapper maps each row to an entity.
   * @param <T>    entity type.
   * @return list of mapped entities, possibly empty.
   * @throws SQLException on DB error.
   */
  protected <T> List<T> queryMany(String sql, StatementSetter setter, RowMapper<T> mapper) throws SQLException {
    List<T> results = new ArrayList<>();

    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      setter.set(ps);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          results.add(mapper.map(rs));
        }
      }
    }
    return results;
  }

  /**
   * runs an INSERT / UPDATE / DELETE.
   *
   * @param sql    SQL with ? placeholders.
   * @param setter binds parameters.
   * @return number of affected rows.
   * @throws SQLException on DB error.
   */
  protected int executeUpdate(String sql, StatementSetter setter) throws SQLException {
    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      setter.set(ps);
      return ps.executeUpdate();
    }
  }

  /**
   * runs an INSERT and returns the first auto-generated key.
   *
   * @param sql    INSERT SQL with ? placeholders.
   * @param setter binds parameters.
   * @return the auto-generated id, or -1 if none was produced.
   * @throws SQLException on DB error.
   */
  protected int executeInsertReturningKey(String sql, StatementSetter setter) throws SQLException {
    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      setter.set(ps);
      ps.executeUpdate();

      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
          return keys.getInt(1);
        }
      }
    }
    return -1;
  }
}
