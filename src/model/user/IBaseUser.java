package model.user;

import java.time.LocalDateTime;

/**
 * Read-only view of a user entity. Both StandardUser and AdminUser
 * implement this (via BaseUserAbstract). There are no setters because
 * user entities are immutable once built - any update produces a new
 * instance. The isAdmin() flag tells the two concrete subclasses apart.
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
