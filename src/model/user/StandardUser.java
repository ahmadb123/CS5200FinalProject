package model.user;

import java.time.LocalDateTime;

/**
 * Regular (non-admin) user entity. Inherits the shared user fields from
 * BaseUserAbstract and adds a private constructor and nested Builder so
 * every instance is built immutably. isAdmin() always returns false.
 * BaseUserAbstract.of(...) picks this subclass when the is_admin column
 * of a DB row is 0.
 */
public class StandardUser extends BaseUserAbstract {

  private StandardUser(int userId, String name, String email, String oauth,
                      LocalDateTime createdAt) {
    super(userId, name, email, oauth, createdAt);
  }

  @Override
  public boolean isAdmin() {
    return false;
  }

  public Builder builder() {
    return new Builder()
        .withUserId(this.userId)
        .withName(this.name)
        .withEmail(this.email)
        .withOauth(this.oauthProvider)
        .withCreatedAt(this.createdAt);
  }

  public static class Builder {
    private int userId;
    private String name;
    private String email;
    private String oauth;
    private LocalDateTime createdAt;

    public Builder withUserId(int userId) {
      this.userId = userId;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder withOauth(String oauth) {
      this.oauth = oauth;
      return this;
    }

    public Builder withCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public StandardUser build() {
      return new StandardUser(userId, name, email, oauth, createdAt);
    }
  }
}
