package model.user;

import java.time.LocalDateTime;

public abstract class BaseUserAbstract implements IBaseUser {
  protected final int userId;
  protected final String name;
  protected final String email;
  protected final String oauthProvider;
  protected final LocalDateTime createdAt;

  protected BaseUserAbstract(int userId, String name, String email,
                             String oauthProvider, LocalDateTime createdAt) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.oauthProvider = oauthProvider;
    this.createdAt = createdAt;
  }

  @Override
  public int getUserId() {
    return userId;
  }

  @Override
  public String getName() {
    return name;
  }


  @Override
  public String getEmail() {
    return email;
  }


  @Override
  public String getOauthProvider() {
    return oauthProvider;
  }


  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }


  @Override
  public abstract boolean isAdmin();

  public static IBaseUser of(int userId, String name,
                             String email, String oauth, boolean isAdmin,
                             LocalDateTime createdAt) {
    if (isAdmin) {
      return new AdminUser.AdminBuilder()
          .withUserId(userId)
          .withName(name)
          .withEmail(email)
          .withOauth(oauth)
          .withCreatedAt(createdAt)
          .build();
    } else {
      return new StandardUser.Builder()
          .withUserId(userId)
          .withName(name)
          .withEmail(email)
          .withOauth(oauth)
          .withCreatedAt(createdAt)
          .build();
    }
  }
}
