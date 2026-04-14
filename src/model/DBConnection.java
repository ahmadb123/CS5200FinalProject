package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection helper. Keeps the MySQL URL, username, and password
 * as constants and hands out a new Connection each time a model asks
 * for one. Every model takes a DBConnection through its constructor
 * (dependency injection) and uses getConnection() inside a
 * try-with-resources block for each query. Keeping the credentials in a
 * single file means switching environments only touches this class.
 */
public class DBConnection {
  private static final String URL = "jdbc:mysql://localhost:3306/worldbuy";
  private static final String USER = "root";
  private static final String PASSWORD = "";

  /**
   * Opens a new connection to the worldbuy database. The caller owns the
   * returned connection and is responsible for closing it (usually in a
   * try-with-resources block).
   *
   * @return a new JDBC connection.
   * @throws SQLException if the connection cannot be established.
   */
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
