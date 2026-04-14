package model.orderLog;

import java.time.LocalDateTime;

/**
 * Order log entity. One row in the order audit trail, with a log id, an
 * order id, an action type (for example "STATUS_CHANGE"), a
 * human-readable description, and a creation timestamp. Rows are
 * append-only - once written they are never updated or deleted on their
 * own. Immutable; built through the nested Builder.
 */
public class OrderLog {
  private final int logId;
  private final int orderId;
  private final String actionType;
  private final String actionDescription;
  private final LocalDateTime createdAt;

  private OrderLog(int logId, int orderId, String actionType,
                   String actionDescription, LocalDateTime createdAt) {
    this.logId = logId;
    this.orderId = orderId;
    this.actionType = actionType;
    this.actionDescription = actionDescription;
    this.createdAt = createdAt;
  }

  public int getLogId() {
    return logId;
  }

  public int getOrderId() {
    return orderId;
  }

  public String getActionType() {
    return actionType;
  }

  public String getActionDescription() {
    return actionDescription;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public Builder toBuilder() {
    return new Builder()
        .withLogId(this.logId)
        .withOrderId(this.orderId)
        .withActionType(this.actionType)
        .withActionDescription(this.actionDescription)
        .withCreatedAt(this.createdAt);
  }

  public static class Builder {
    private int logId;
    private int orderId;
    private String actionType;
    private String actionDescription;
    private LocalDateTime createdAt;

    public Builder withLogId(int logId) {
      this.logId = logId;
      return this;
    }

    public Builder withOrderId(int orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder withActionType(String actionType) {
      this.actionType = actionType;
      return this;
    }

    public Builder withActionDescription(String actionDescription) {
      this.actionDescription = actionDescription;
      return this;
    }

    public Builder withCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public OrderLog build() {
      return new OrderLog(logId, orderId, actionType, actionDescription, createdAt);
    }
  }
}
