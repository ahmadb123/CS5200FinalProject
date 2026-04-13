package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection helper. holds the MySQL URL, user, and password as constants
 * and hands out fresh Connection objects on demand. every model receives this
 * via its constructor (dependency injection) and calls getConnection() inside
 * a try-with-resources block for each query. keeping DB credentials in one
 * place means swapping environments (local vs testing) only touches this file.
 */
public class DBConnection {
  private static final String URL = "jdbc:mysql://localhost:3306/worldbuy";
  private static final String USER = "root";
  private static final String PASSWORD = "";

  /**
   * opens a new connection to the worldbuy database. caller is responsible
   * for closing it, typically via try-with-resources.
   *
   * @return a fresh JDBC connection.
   * @throws SQLException if the connection cannot be established.
   */
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
