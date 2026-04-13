package model.user;

import java.time.LocalDateTime;

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
