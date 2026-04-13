package model.user;

import java.time.LocalDateTime;

/**
 * Skeletal base class for user entities. holds the 5 shared fields
 * (userId, name, email, oauthProvider, createdAt) as final protected fields
 * and their getter implementations. subclasses (StandardUser / AdminUser)
 * supply their own private constructor + Builder and implement isAdmin()
 * to discriminate roles. the static factory of(...) maps the DB is_admin
 * flag to the right subclass at row-mapping time.
 */
public abstract class BaseUserAbstract implements IBaseUser {
  protected final int userId;
  protected final String name;
  protected final String email;
  protected final String oauthProvider;
  protected final LocalDateTime createdAt;

  /**
   * protected constructor used only by subclasses via super(...). enforces
   * full-arg construction since all fields are final.
   *
   * @param userId        primary key.
   * @param name          display name.
   * @param email         unique email.
   * @param oauthProvider oauth provider label.
   * @param createdAt     creation timestamp.
   */
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

  /**
   * role discriminator — implemented by each subclass.
   *
   * @return true if this user is an admin.
   */
  @Override
  public abstract boolean isAdmin();

  /**
   * factory method. builds the appropriate concrete subclass based on
   * the is_admin flag. centralizes the role-selection logic so JDBC row
   * mappers have one place to call instead of branching at each mapper.
   *
   * @param userId    primary key.
   * @param name      display name.
   * @param email     email.
   * @param oauth     oauth provider label.
   * @param isAdmin   true to build an AdminUser, false for StandardUser.
   * @param createdAt creation timestamp.
   * @return a new IBaseUser of the appropriate concrete type.
   */
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
