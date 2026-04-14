
package model.exchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Exchange rate model interface. Stores currency codes and their exchange
 * rates, and exposes a convertAmount helper that calls the convert_currency
 * SQL function.
 */
public interface IExchangeRateModel {

  /**
   * inserts a new exchange rate entry.
   *
   * @param currency currency code (e.g. "USD", "EUR").
   * @param rate     exchange rate relative to base currency.
   * @return the new rate with DB-assigned id and timestamp.
   * @throws SQLException on DB error.
   */
  ExchangeRate addRate(String currency, BigDecimal rate) throws SQLException;

  /**
   * @param rateId rate to look up.
   * @return the matching rate, or null.
   * @throws SQLException on DB error.
   */
  ExchangeRate findRateById(int rateId) throws SQLException;

  /**
   * @param currency currency code to look up.
   * @return the matching rate, or null if not found.
   * @throws SQLException on DB error.
   */
  ExchangeRate findRateByCurrency(String currency) throws SQLException;

  /**
   * @return every exchange rate in the system.
   * @throws SQLException on DB error.
   */
  List<ExchangeRate> findAllRates() throws SQLException;

  /**
   * updates an existing rate and stamps updated_at = NOW().
   *
   * @param currency currency code to update.
   * @param newRate  new exchange rate value.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean updateRate(String currency, BigDecimal newRate) throws SQLException;

  /**
   * @param rateId rate to delete.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deleteRate(int rateId) throws SQLException;

  /**
   * calls the convert_currency SQL function to convert an amount from base
   * currency into a target currency. returns the original amount if the
   * target currency is not found in the table.
   *
   * @param amount   amount in base currency.
   * @param currency target currency code.
   * @return converted amount, or amount unchanged if currency not found.
   * @throws SQLException on DB error.
   */
  BigDecimal convertAmount(BigDecimal amount, String currency) throws SQLException;
}
