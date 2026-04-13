package model.shipment;

import java.sql.SQLException;
import java.util.List;

/**
 * Contract for the shipment model. like payment, shipments have a 1:1
 * relationship with orders (unique order_id). shipments start as 'preparing'
 * and transition through 'shipped' and 'delivered' with timestamps stamped
 * at each status change.
 */
public interface IShipmentModel {

  /**
   * creates a new shipment row with status 'preparing' and null timestamps.
   *
   * @param orderId        order this shipment is for.
   * @param trackingNumber carrier tracking number (string — can contain letters).
   * @param carrier        carrier name (e.g. "UPS", "FedEx", "USPS").
   * @return the new shipment with DB-assigned id.
   * @throws SQLException on DB error.
   */
  Shipment createShipment(int orderId, String trackingNumber, String carrier) throws SQLException;

  /**
   * @param shipmentId shipment to look up.
   * @return the matching shipment, or null.
   * @throws SQLException on DB error.
   */
  Shipment findShipmentById(int shipmentId) throws SQLException;

  /**
   * 1:1 lookup by order.
   *
   * @param orderId order to look up.
   * @return that order's shipment, or null.
   * @throws SQLException on DB error.
   */
  Shipment findShipmentByOrderId(int orderId) throws SQLException;

  /**
   * @return every shipment in the system.
   * @throws SQLException on DB error.
   */
  List<Shipment> findAllShipments() throws SQLException;

  /**
   * sets status to 'shipped' and stamps shipped_at = NOW().
   *
   * @param shipmentId shipment to mark shipped.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean markAsShipped(int shipmentId) throws SQLException;

  /**
   * sets status to 'delivered' and stamps delivered_at = NOW().
   *
   * @param shipmentId shipment to mark delivered.
   * @return true if a row was updated.
   * @throws SQLException on DB error.
   */
  boolean markAsDelivered(int shipmentId) throws SQLException;

  /**
   * @param shipmentId shipment to delete.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deleteShipment(int shipmentId) throws SQLException;
}
