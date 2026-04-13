package model.user;

import java.sql.SQLException;

/**
 * Contract for the user model — the 6 account-level operations a regular user
 * can invoke. narrower than IAdminModel so ISP-respecting code can hold this
 * reference type and be prevented (at compile time) from calling admin methods.
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
   * ends the user's session. no DB work; kept here so the UI has one place
   * to call for logout symmetry.
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
