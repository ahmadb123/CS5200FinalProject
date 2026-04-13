package model.product;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DBConnection;

public class ProductModel implements IProductModel {
  private final DBConnection database;

  public ProductModel(DBConnection db) {
    this.database = db;
  }

  @Override
  public Product addProduct(String url, String name, String spec, BigDecimal price) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL add_product(?, ?, ?, ?)}")) {

      cs.setString(1, url);
      cs.setString(2, name);
      cs.setString(3, spec);
      cs.setBigDecimal(4, price);

      if (cs.execute()) {
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return mapRow(rs);
          }
        }
      }
    }
    return null;
  }

  @Override
  public Product findProductById(int productId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_product_by_id(?)}")) {

      cs.setInt(1, productId);

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<Product> findAllProducts() throws SQLException {
    List<Product> products = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_all_products()}")) {

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          products.add(mapRow(rs));
        }
      }
    }
    return products;
  }

  @Override
  public List<Product> searchByName(String keyword) throws SQLException {
    List<Product> products = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL search_products(?)}")) {

      cs.setString(1, keyword);

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          products.add(mapRow(rs));
        }
      }
    }
    return products;
  }

  @Override
  public boolean updateProduct(Product product) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL update_product(?, ?, ?, ?, ?)}")) {

      cs.setInt(1, product.getProductId());
      cs.setString(2, product.getProductUrl());
      cs.setString(3, product.getProductName());
      cs.setString(4, product.getSpecification());
      cs.setBigDecimal(5, product.getUnitPrice());

      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean updatePrice(int productId, BigDecimal newPrice) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL update_product_price(?, ?)}")) {

      cs.setInt(1, productId);
      cs.setBigDecimal(2, newPrice);

      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public boolean deleteProduct(int productId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL delete_product(?)}")) {

      cs.setInt(1, productId);
      return cs.executeUpdate() > 0;
    }
  }

  private Product mapRow(ResultSet rs) throws SQLException {
    return new Product.Builder()
        .withProductId(rs.getInt("product_id"))
        .withProductUrl(rs.getString("product_url"))
        .withProductName(rs.getString("product_name"))
        .withSpecification(rs.getString("specification"))
        .withUnitPrice(rs.getBigDecimal("unit_price"))
        .build();
  }
}
