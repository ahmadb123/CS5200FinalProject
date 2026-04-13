package controller.uiFeatures;

/**
 * Admin features interface — the 5 admin-only user-management actions.
 * only the admin dashboard has access to these in the UI. kept separate
 * from ProfileFeatures / AuthFeatures so regular-user menus cannot call them.
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
