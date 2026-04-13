package controller;

import java.sql.SQLException;

/**
 * Generic command contract. each user-invokable action in WorldBuy (register, login,
 * place order, promote user, etc.) is implemented as its own class that implements Icommand.
 * the type parameter T is whatever the command produces (IBaseUser, Order, Boolean, Void, etc.).
 * holds its dependencies via its constructor; execute() runs the action once.
 *
 * @param <T> the type returned by execute().
 */
public interface Icommand<T> {

  /**
   * runs the command against whatever model it was constructed with.
   *
   * @return the result of the action (varies per command).
   * @throws SQLException if the underlying database operation fails.
   */
  T execute() throws SQLException;
}
