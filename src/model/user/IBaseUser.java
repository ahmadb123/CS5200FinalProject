package model.user;

import java.time.LocalDateTime;

/**
 * Read-only contract for a user entity. both StandardUser and AdminUser
 * (via BaseUserAbstract) implement this. the interface has no setters because
 * user entities are immutable once constructed — updates produce new instances.
 * isAdmin() is the discriminator that distinguishes the two concrete subclasses.
 */
public interface IBaseUser {

  /**
   * @return the user's auto-generated primary key.
   */
  int getUserId();

  /**
   * @return the user's display name.
   */
  String getName();

  /**
   * @return the user's email address (unique in the DB).
   */
  String getEmail();

  /**
   * @return oauth provider label (e.g. "google", "local").
   */
  String getOauthProvider();

  /**
   * @return when the account was created.
   */
  LocalDateTime getCreatedAt();

  /**
   * @return true if this user is an administrator.
   */
  boolean isAdmin();
}
