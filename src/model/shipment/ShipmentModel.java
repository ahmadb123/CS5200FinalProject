package model.shipment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import model.AbstractJdbcModel;
import model.DBConnection;

/**
 * Concrete JDBC implementation of IShipmentModel. uses inline SQL via
 * AbstractJdbcModel's helper methods (queryOne, queryMany, executeUpdate,
 * executeInsertReturningKey) rather than stored procedures. all SQL is
 * extracted to private static final constants at the top for readability.
 * demonstrates the alternative "no stored proc" path alongside the
 * CallableStatement-heavy User/Order/Product/Payment models.
 */
public class ShipmentModel extends AbstractJdbcModel implements IShipmentModel {

  private static final String SQL_INSERT =
      "INSERT INTO shipments (order_id, tracking_number, carrier, shipment_status, "
          + "shipped_at, delivered_at) VALUES (?, ?, ?, 'preparing', NULL, NULL)";

  private static final String SQL_SELECT_BY_ID =
      "SELECT shipment_id, order_id, tracking_number, carrier, shipment_status, "
          + "shipped_at, delivered_at FROM shipments WHERE shipment_id = ?";

  private static final String SQL_SELECT_BY_ORDER_ID =
      "SELECT shipment_id, order_id, tracking_number, carrier, shipment_status, "
          + "shipped_at, delivered_at FROM shipments WHERE order_id = ?";

  private static final String SQL_SELECT_ALL =
      "SELECT shipment_id, order_id, tracking_number, carrier, shipment_status, "
          + "shipped_at, delivered_at FROM shipments ORDER BY shipment_id DESC";

  private static final String SQL_MARK_SHIPPED =
      "UPDATE shipments SET shipment_status = 'shipped', shipped_at = NOW() "
          + "WHERE shipment_id = ?";

  private static final String SQL_MARK_DELIVERED =
      "UPDATE shipments SET shipment_status = 'delivered', delivered_at = NOW() "
          + "WHERE shipment_id = ?";

  private static final String SQL_DELETE =
      "DELETE FROM shipments WHERE shipment_id = ?";

  public ShipmentModel(DBConnection db) {
    super(db);
  }

  @Override
  public Shipment createShipment(int orderId, String trackingNumber, String carrier)
      throws SQLException {
    int newId = executeInsertReturningKey(SQL_INSERT, ps -> {
      ps.setInt(1, orderId);
      ps.setString(2, trackingNumber);
      ps.setString(3, carrier);
    });
    return newId > 0 ? findShipmentById(newId) : null;
  }

  @Override
  public Shipment findShipmentById(int shipmentId) throws SQLException {
    return queryOne(SQL_SELECT_BY_ID,
        ps -> ps.setInt(1, shipmentId),
        this::mapRow);
  }

  @Override
  public Shipment findShipmentByOrderId(int orderId) throws SQLException {
    return queryOne(SQL_SELECT_BY_ORDER_ID,
        ps -> ps.setInt(1, orderId),
        this::mapRow);
  }

  @Override
  public List<Shipment> findAllShipments() throws SQLException {
    return queryMany(SQL_SELECT_ALL, ps -> {}, this::mapRow);
  }

  @Override
  public boolean markAsShipped(int shipmentId) throws SQLException {
    return executeUpdate(SQL_MARK_SHIPPED, ps -> ps.setInt(1, shipmentId)) > 0;
  }

  @Override
  public boolean markAsDelivered(int shipmentId) throws SQLException {
    return executeUpdate(SQL_MARK_DELIVERED, ps -> ps.setInt(1, shipmentId)) > 0;
  }

  @Override
  public boolean deleteShipment(int shipmentId) throws SQLException {
    return executeUpdate(SQL_DELETE, ps -> ps.setInt(1, shipmentId)) > 0;
  }

  private Shipment mapRow(ResultSet rs) throws SQLException {
    Timestamp shippedTs = rs.getTimestamp("shipped_at");
    Timestamp deliveredTs = rs.getTimestamp("delivered_at");

    return new Shipment.Builder()
        .withShipmentId(rs.getInt("shipment_id"))
        .withOrderId(rs.getInt("order_id"))
        .withTrackingNumber(rs.getString("tracking_number"))
        .withCarrier(rs.getString("carrier"))
        .withShipmentStatus(rs.getString("shipment_status"))
        .withShippedAt(shippedTs != null ? shippedTs.toLocalDateTime() : null)
        .withDeliveredAt(deliveredTs != null ? deliveredTs.toLocalDateTime() : null)
        .build();
  }
}
