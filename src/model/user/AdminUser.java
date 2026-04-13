package model.user;

import java.time.LocalDateTime;

/**
 * Admin user entity. extends BaseUserAbstract for the 5 shared fields and
 * supplies its own nested Builder. isAdmin() always returns true. the DB row
 * is mapped to this subclass when is_admin is 1, via BaseUserAbstract.of(...).
 * functionally identical to StandardUser data-wise — the distinction is the
 * polymorphic isAdmin() return value used by controllers and views for role
 * checks.
 */
public class AdminUser extends BaseUserAbstract {
  private AdminUser(int userId, String name, String email, String oauth, LocalDateTime createdAt) {
    super(userId, name, email, oauth, createdAt);
  }

  @Override
  public boolean isAdmin() {
    return true;
  }

  public AdminBuilder builder() {
    return new AdminBuilder()
        .withUserId(this.userId)
        .withName(this.name)
        .withEmail(this.email)
        .withOauth(this.oauthProvider)
        .withCreatedAt(this.createdAt);
  }

  public static class AdminBuilder {
    private int userId;
    private String name;
    private String email;
    private String oauth;
    private LocalDateTime createdAt;

    public AdminBuilder withUserId(int userId) {
      this.userId = userId;
      return this;
    }

    public AdminBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public AdminBuilder withEmail(String email) {
      this.email = email;
      return this;
    }

    public AdminBuilder withOauth(String oauth) {
      this.oauth = oauth;
      return this;
    }

    public AdminBuilder withCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public AdminUser build() {
      return new AdminUser(userId, name, email, oauth, createdAt);
    }
  }
}

