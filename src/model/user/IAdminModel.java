package model.user;

import java.sql.SQLException;
import java.util.List;

/**
 * Admin contract — extends IUserModel with 6 admin-only user management operations.
 * admins can still do everything a regular user can (inherited from IUserModel)
 * plus the admin-specific actions declared here. the one concrete UserModel class
 * implements this interface; the controller hands it out as IUserModel or IAdminModel
 * depending on the caller's role.
 */
public interface IAdminModel extends IUserModel {

  /**
   * @return every user in the system.
   * @throws SQLException on DB error.
   */
  List<IBaseUser> viewAllUsers() throws SQLException;

  /**
   * admin lookup — get any user by id (not just self).
   *
   * @param userId id to look up.
   * @return the matching user, or null.
   * @throws SQLException on DB error.
   */
  IBaseUser findUserById(int userId) throws SQLException;

  /**
   * flips the is_admin flag to true and returns the refreshed user.
   *
   * @param userId target user.
   * @return the updated user (now an AdminUser instance via factory).
   * @throws SQLException on DB error.
   */
  IBaseUser promoteToAdmin(int userId) throws SQLException;

  /**
   * flips the is_admin flag to false and returns the refreshed user.
   *
   * @param userId target user.
   * @return the updated user (now a StandardUser instance via factory).
   * @throws SQLException on DB error.
   */
  IBaseUser demoteFromAdmin(int userId) throws SQLException;

  /**
   * hard-deletes a user by id. cascades to their orders via FK.
   *
   * @param userId target user.
   * @return true if a row was deleted.
   * @throws SQLException on DB error.
   */
  boolean deleteAnyUser(int userId) throws SQLException;

  /**
   * @return total number of users in the system.
   * @throws SQLException on DB error.
   */
  int countUsers() throws SQLException;
}
