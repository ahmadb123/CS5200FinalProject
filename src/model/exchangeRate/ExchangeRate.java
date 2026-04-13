package model.exchangeRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Exchange rate entity. 4 fields: rateId, currency (code like "USD"),
 * exchangeRate (decimal), updatedAt (last change timestamp). used for
 * converting prices between currencies via the convert_currency SQL
 * function. immutable, constructed via the nested Builder.
 */
public class ExchangeRate {
  private final int rateId;
  private final String currency;
  private final BigDecimal exchangeRate;
  private final LocalDateTime updatedAt;

  private ExchangeRate(int rateId, String currency, BigDecimal exchangeRate, LocalDateTime updatedAt) {
    this.rateId = rateId;
    this.currency = currency;
    this.exchangeRate = exchangeRate;
    this.updatedAt = updatedAt;
  }

  public int getRateId() {
    return rateId;
  }

  public String getCurrency() {
    return currency;
  }

  public BigDecimal getExchangeRate() {
    return exchangeRate;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public Builder toBuilder() {
    return new Builder()
        .withRateId(this.rateId)
        .withCurrency(this.currency)
        .withExchangeRate(this.exchangeRate)
        .withUpdatedAt(this.updatedAt);
  }

  public static class Builder {
    private int rateId;
    private String currency;
    private BigDecimal exchangeRate;
    private LocalDateTime updatedAt;

    public Builder withRateId(int rateId) {
      this.rateId = rateId;
      return this;
    }

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withExchangeRate(BigDecimal exchangeRate) {
      this.exchangeRate = exchangeRate;
      return this;
    }

    public Builder withUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public ExchangeRate build() {
      return new ExchangeRate(rateId, currency, exchangeRate, updatedAt);
    }
  }
}
