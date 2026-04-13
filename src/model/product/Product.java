package model.product;

import java.math.BigDecimal;

public class Product {
  private final int productId;
  private final String productUrl;
  private final String productName;
  private final String specification;
  private final BigDecimal unitPrice;

  private Product(int productId, String productUrl, String productName,
                  String specification, BigDecimal unitPrice) {
    this.productId = productId;
    this.productUrl = productUrl;
    this.productName = productName;
    this.specification = specification;
    this.unitPrice = unitPrice;
  }

  public int getProductId() {
    return productId;
  }

  public String getProductUrl() {
    return productUrl;
  }

  public String getProductName() {
    return productName;
  }

  public String getSpecification() {
    return specification;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public Builder toBuilder() {
    return new Builder()
        .withProductId(this.productId)
        .withProductUrl(this.productUrl)
        .withProductName(this.productName)
        .withSpecification(this.specification)
        .withUnitPrice(this.unitPrice);
  }

  public static class Builder {
    private int productId;
    private String productUrl;
    private String productName;
    private String specification;
    private BigDecimal unitPrice;

    public Builder withProductId(int productId) {
      this.productId = productId;
      return this;
    }

    public Builder withProductUrl(String productUrl) {
      this.productUrl = productUrl;
      return this;
    }

    public Builder withProductName(String productName) {
      this.productName = productName;
      return this;
    }

    public Builder withSpecification(String specification) {
      this.specification = specification;
      return this;
    }

    public Builder withUnitPrice(BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
      return this;
    }

    public Product build() {
      return new Product(productId, productUrl, productName, specification, unitPrice);
    }
  }
}
