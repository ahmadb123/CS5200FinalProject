package model.emailNotification;

import java.time.LocalDateTime;

/**
 * Email notification entity. A record that a message was sent to a user
 * about a particular order. Holds a notification id, the related order
 * id, the recipient email, the subject, a sentAt timestamp, and a
 * delivery status ("sent", "failed", and so on). Immutable; built
 * through the nested Builder.
 */
public class EmailNotification {
  private final int notificationId;
  private final int orderId;
  private final String email;
  private final String subject;
  private final LocalDateTime sentAt;
  private final String status;

  private EmailNotification(int notificationId, int orderId, String email,
                            String subject, LocalDateTime sentAt, String status) {
    this.notificationId = notificationId;
    this.orderId = orderId;
    this.email = email;
    this.subject = subject;
    this.sentAt = sentAt;
    this.status = status;
  }

  public int getNotificationId() {
    return notificationId;
  }

  public int getOrderId() {
    return orderId;
  }

  public String getEmail() {
    return email;
  }

  public String getSubject() {
    return subject;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public String getStatus() {
    return status;
  }

  public Builder toBuilder() {
    return new Builder()
        .withNotificationId(this.notificationId)
        .withOrderId(this.orderId)
        .withEmail(this.email)
        .withSubject(this.subject)
        .withSentAt(this.sentAt)
        .withStatus(this.status);
  }

  public static class Builder {
    private int notificationId;
    private int orderId;
    private String email;
    private String subject;
    private LocalDateTime sentAt;
    private String status;

    public Builder withNotificationId(int notificationId) {
      this.notificationId = notificationId;
      return this;
    }

    public Builder withOrderId(int orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder withEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder withSentAt(LocalDateTime sentAt) {
      this.sentAt = sentAt;
      return this;
    }

    public Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    public EmailNotification build() {
      return new EmailNotification(notificationId, orderId, email, subject, sentAt, status);
    }
  }
}
