package controller.uiFeatures;

/**
 * Admin-only user management actions. Only the admin dashboard reaches
 * these in the UI. They live in their own interface (rather than in
 * ProfileFeatures or AuthFeatures) so regular-user menus have no way to
 * call them.
 */
public interface AdminFeatures {

  /**
   * loads every user in the system (admin view).
   */
  void viewAllUsers();

  /**
   * promotes a regular user to admin.
   *
   * @param userId target user.
   */
  void promoteToAdmin(int userId);

  /**
   * demotes an admin back to a regular user.
   *
   * @param userId target user.
   */
  void demoteFromAdmin(int userId);

  /**
   * hard-deletes any user by id (cascades to their orders).
   *
   * @param userId target user.
   */
  void deleteAnyUser(int userId);

  /**
   * returns the total number of users as a status message.
   */
  void countUsers();
}
