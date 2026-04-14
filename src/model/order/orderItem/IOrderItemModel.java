package model.order.orderItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Order item (cart) model interface. The order_items table has a composite
 * primary key (order_id, product_id), so lookups take an (orderId, productId)
 * pair or a find-by-order call. Subtotals are computed by a DB trigger on
 * insert/update.
 */
public interface IOrderItemModel {

  /**
   * adds a product to an order. subtotal is set automatically by the trigger.
   *
   * @param orderId   order to attach the item to.
   * @param productId product being added.
   * @param quantity  how many of the product.
   * @param unitPrice price per unit at time of purchase.
   * @return the persisted order item (includes the trigger-computed subtotal).
   * @throws SQLException on DB error.
   */
  OrderItem addItem(int orderId, int productId, int quantity, BigDecimal unitPrice)
      throws SQLException;

  /**
   * looks up a specific line item by composite key.
   *
   * @param orderId   order containing the item.
   * @param productId product in the item.
   * @return the matching item, or null.
   * @throws SQLException on DB error.
   */
  OrderItem findItem(int orderId, int productId) throws SQLException;

  /**
   * returns all items for an order (i.e. the full cart / order contents).
   *
   * @param orderId order to read.
   * @return list of items, possibly empty.
   * @throws SQLException on DB error.
   */
  List<OrderItem> findItemsByOrder(int orderId) throws SQLException;

  /**
   * updates the quantity of an existing item. subtotal is recomputed by trigger.
   *
   * @param orderId     order containing the item.
   * @param productId   product to adjust.
   * @param newQuantity new quantity.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean updateQuantity(int orderId, int productId, int newQuantity) throws SQLException;

  /**
   * removes a single item from an order.
   *
   * @param orderId   order containing the item.
   * @param productId product to remove.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean removeItem(int orderId, int productId) throws SQLException;

  /**
   * wipes every item from an order (clear the cart).
   *
   * @param orderId order to clear.
   * @return true if at least one row was deleted.
   * @throws SQLException on DB error.
   */
  boolean clearOrder(int orderId) throws SQLException;
}
