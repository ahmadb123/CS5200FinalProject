package model.product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Contract for the product model. covers browsing the catalog, searching by name,
 * submitting new products (called "product requests" in the user flow), and
 * updating / deleting products. there is no role split — both regular users
 * and admins use these methods, matching the project spec that says users can
 * submit their own product requests.
 */
public interface IProductModel {

  /**
   * inserts a new product and returns the row with the generated id.
   *
   * @param url   product URL.
   * @param name  product name.
   * @param spec  specification / description.
   * @param price unit price.
   * @return the new product with DB-assigned id.
   * @throws SQLException on DB error.
   */
  Product addProduct(String url, String name, String spec, BigDecimal price) throws SQLException;

  /**
   * @param productId product to look up.
   * @return the matching product, or null.
   * @throws SQLException on DB error.
   */
  Product findProductById(int productId) throws SQLException;

  /**
   * @return every product in the catalog.
   * @throws SQLException on DB error.
   */
  List<Product> findAllProducts() throws SQLException;

  /**
   * case-insensitive LIKE search on product_name.
   *
   * @param keyword substring to match.
   * @return products whose name contains the keyword.
   * @throws SQLException on DB error.
   */
  List<Product> searchByName(String keyword) throws SQLException;

  /**
   * applies all editable fields of the given product to the DB.
   *
   * @param product product with updated field values.
   * @return true if the UPDATE affected a row.
   * @throws SQLException on DB error.
   */
  boolean updateProduct(Product product) throws SQLException;

  /**
   * admin convenience — update only the price.
   *
   * @param productId product to update.
   * @param newPrice  new unit price.
   * @return true on success.
   * @throws SQLException on DB error.
   */
  boolean updatePrice(int productId, BigDecimal newPrice) throws SQLException;

  /**
   * @param productId product to delete.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deleteProduct(int productId) throws SQLException;
}
