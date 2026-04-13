package model.shipment;

import java.time.LocalDateTime;

/**
 * Shipment entity. 7 fields: shipmentId, orderId (FK, UNIQUE for 1:1),
 * trackingNumber, carrier, shipmentStatus, shippedAt, deliveredAt.
 * both timestamps are nullable — shippedAt is set when status becomes
 * 'shipped', deliveredAt when it becomes 'delivered'. immutable,
 * constructed via the nested Builder.
 */
public class Shipment {
  private final int shipmentId;
  private final int orderId;
  private final String trackingNumber;
  private final String carrier;
  private final String shipmentStatus;
  private final LocalDateTime shippedAt;
  private final LocalDateTime deliveredAt;

  private Shipment(int shipmentId, int orderId, String trackingNumber, String carrier,
                   String shipmentStatus, LocalDateTime shippedAt, LocalDateTime deliveredAt) {
    this.shipmentId = shipmentId;
    this.orderId = orderId;
    this.trackingNumber = trackingNumber;
    this.carrier = carrier;
    this.shipmentStatus = shipmentStatus;
    this.shippedAt = shippedAt;
    this.deliveredAt = deliveredAt;
  }

  public int getShipmentId() {
    return shipmentId;
  }

  public int getOrderId() {
    return orderId;
  }

  public String getTrackingNumber() {
    return trackingNumber;
  }

  public String getCarrier() {
    return carrier;
  }

  public String getShipmentStatus() {
    return shipmentStatus;
  }

  public LocalDateTime getShippedAt() {
    return shippedAt;
  }

  public LocalDateTime getDeliveredAt() {
    return deliveredAt;
  }

  public Builder toBuilder() {
    return new Builder()
        .withShipmentId(this.shipmentId)
        .withOrderId(this.orderId)
        .withTrackingNumber(this.trackingNumber)
        .withCarrier(this.carrier)
        .withShipmentStatus(this.shipmentStatus)
        .withShippedAt(this.shippedAt)
        .withDeliveredAt(this.deliveredAt);
  }

  public static class Builder {
    private int shipmentId;
    private int orderId;
    private String trackingNumber;
    private String carrier;
    private String shipmentStatus;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public Builder withShipmentId(int shipmentId) {
      this.shipmentId = shipmentId;
      return this;
    }

    public Builder withOrderId(int orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder withTrackingNumber(String trackingNumber) {
      this.trackingNumber = trackingNumber;
      return this;
    }

    public Builder withCarrier(String carrier) {
      this.carrier = carrier;
      return this;
    }

    public Builder withShipmentStatus(String shipmentStatus) {
      this.shipmentStatus = shipmentStatus;
      return this;
    }

    public Builder withShippedAt(LocalDateTime shippedAt) {
      this.shippedAt = shippedAt;
      return this;
    }

    public Builder withDeliveredAt(LocalDateTime deliveredAt) {
      this.deliveredAt = deliveredAt;
      return this;
    }

    public Shipment build() {
      return new Shipment(shipmentId, orderId, trackingNumber, carrier,
                          shipmentStatus, shippedAt, deliveredAt);
    }
  }
}
