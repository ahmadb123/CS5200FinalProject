package model.user;

import java.sql.SQLException;

/**
 * User model interface. The account-level operations that a regular user
 * can call. This is a narrower view than IAdminModel, so code that only
 * needs regular-user access can hold an IUserModel reference and the
 * compiler will refuse any call to an admin-only method.
 */
public interface IUserModel {

  /**
   * creates a new regular user account.
   *
   * @param name  display name.
   * @param email unique email.
   * @param oauth oauth provider label.
   * @return the new user with DB-assigned id and timestamp; null on failure.
   * @throws SQLException if the insert fails (e.g. duplicate email).
   */
  IBaseUser register(String name, String email, String oauth) throws SQLException;

  /**
   * authenticates a user by email.
   *
   * @param email email to look up.
   * @return the matching user, or null if none exists.
   * @throws SQLException on DB error.
   */
  IBaseUser login(String email) throws SQLException;

  /**
   * Ends the user's session. Does no DB work; it lives on the interface
   * so the UI has a single method to call when logging out.
   *
   * @param userId id of the user logging out.
   */
  void logout(int userId);

  /**
   * loads a user by id.
   *
   * @param userId id to look up.
   * @return the matching user, or null.
   * @throws SQLException on DB error.
   */
  IBaseUser viewProfile(int userId) throws SQLException;

  /**
   * persists edited profile fields (name, email, oauth).
   *
   * @param updatedUser user entity carrying the new values.
   * @return the updated user if the UPDATE affected a row; null otherwise.
   * @throws SQLException on DB error.
   */
  IBaseUser updateProfile(IBaseUser updatedUser) throws SQLException;

  /**
   * deletes a user's own account.
   *
   * @param userId id of the user deleting themselves.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deleteAccount(int userId) throws SQLException;
}
