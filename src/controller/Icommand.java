package controller;

import java.sql.SQLException;

/**
 * Generic command interface. Every user-invokable action in WorldBuy
 * (register, login, place order, promote user, etc.) is implemented as
 * its own class that implements Icommand. The type parameter T is
 * whatever the command produces (IBaseUser, Order, Boolean, Void, etc.).
 * A command receives its dependencies through its constructor, and
 * execute() runs the action once.
 *
 * @param <T> the type returned by execute().
 */
public interface Icommand<T> {

  /**
   * Runs the command against whatever model it was constructed with.
   *
   * @return the result of the action (varies per command).
   * @throws SQLException if the underlying database operation fails.
   */
  T execute() throws SQLException;
}
