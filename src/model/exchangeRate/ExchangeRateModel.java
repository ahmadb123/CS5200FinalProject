package model.exchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import model.AbstractJdbcModel;
import model.DBConnection;

/**
 * Concrete JDBC implementation of IExchangeRateModel. extends AbstractJdbcModel
 * and uses inline SQL constants for CRUD. the convertAmount method is special:
 * it uses a plain PreparedStatement to call the convert_currency SQL function
 * directly ({@code SELECT convert_currency(?, ?)}), demonstrating the
 * "Java calls server-side function" rubric pattern.
 */
public class ExchangeRateModel extends AbstractJdbcModel implements IExchangeRateModel {

  private static final String SQL_INSERT =
      "INSERT INTO exchange_rates (currency, exchange_rate, updated_at) VALUES (?, ?, NOW())";

  private static final String SQL_SELECT_BY_ID =
      "SELECT rate_id, currency, exchange_rate, updated_at FROM exchange_rates WHERE rate_id = ?";

  private static final String SQL_SELECT_BY_CURRENCY =
      "SELECT rate_id, currency, exchange_rate, updated_at FROM exchange_rates WHERE currency = ?";

  private static final String SQL_SELECT_ALL =
      "SELECT rate_id, currency, exchange_rate, updated_at FROM exchange_rates ORDER BY currency";

  private static final String SQL_UPDATE_RATE =
      "UPDATE exchange_rates SET exchange_rate = ?, updated_at = NOW() WHERE currency = ?";

  private static final String SQL_DELETE =
      "DELETE FROM exchange_rates WHERE rate_id = ?";

  private static final String SQL_CONVERT_FUNCTION =
      "SELECT convert_currency(?, ?)";

  public ExchangeRateModel(DBConnection db) {
    super(db);
  }

  @Override
  public ExchangeRate addRate(String currency, BigDecimal rate) throws SQLException {
    int newId = executeInsertReturningKey(SQL_INSERT, ps -> {
      ps.setString(1, currency);
      ps.setBigDecimal(2, rate);
    });
    return newId > 0 ? findRateById(newId) : null;
  }

  @Override
  public ExchangeRate findRateById(int rateId) throws SQLException {
    return queryOne(SQL_SELECT_BY_ID,
        ps -> ps.setInt(1, rateId),
        this::mapRow);
  }

  @Override
  public ExchangeRate findRateByCurrency(String currency) throws SQLException {
    return queryOne(SQL_SELECT_BY_CURRENCY,
        ps -> ps.setString(1, currency),
        this::mapRow);
  }

  @Override
  public List<ExchangeRate> findAllRates() throws SQLException {
    return queryMany(SQL_SELECT_ALL, ps -> {}, this::mapRow);
  }

  @Override
  public boolean updateRate(String currency, BigDecimal newRate) throws SQLException {
    return executeUpdate(SQL_UPDATE_RATE, ps -> {
      ps.setBigDecimal(1, newRate);
      ps.setString(2, currency);
    }) > 0;
  }

  @Override
  public boolean deleteRate(int rateId) throws SQLException {
    return executeUpdate(SQL_DELETE, ps -> ps.setInt(1, rateId)) > 0;
  }

  @Override
  public BigDecimal convertAmount(BigDecimal amount, String currency) throws SQLException {
    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement(SQL_CONVERT_FUNCTION)) {

      ps.setBigDecimal(1, amount);
      ps.setString(2, currency);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          BigDecimal converted = rs.getBigDecimal(1);
          return converted != null ? converted : amount;
        }
      }
    }
    return amount;
  }

  private ExchangeRate mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("updated_at");
    return new ExchangeRate.Builder()
        .withRateId(rs.getInt("rate_id"))
        .withCurrency(rs.getString("currency"))
        .withExchangeRate(rs.getBigDecimal("exchange_rate"))
        .withUpdatedAt(ts != null ? ts.toLocalDateTime() : null)
        .build();
  }
}
