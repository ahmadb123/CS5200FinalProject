package model.product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IProductModel {

  Product addProduct(String url, String name, String spec, BigDecimal price) throws SQLException;

  Product findProductById(int productId) throws SQLException;

  List<Product> findAllProducts() throws SQLException;

  List<Product> searchByName(String keyword) throws SQLException;

  boolean updateProduct(Product product) throws SQLException;

  boolean updatePrice(int productId, BigDecimal newPrice) throws SQLException;

  boolean deleteProduct(int productId) throws SQLException;
}
