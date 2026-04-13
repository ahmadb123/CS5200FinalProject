package controller.uiFeatures;

import model.user.IBaseUser;

/**
 * Profile features interface — actions a signed-in user performs on their own account.
 * viewing, updating, and deleting. separate from admin features which operate on other users.
 */
public interface ProfileFeatures {

  /**
   * fetches the signed-in user's profile and pushes it to the view.
   *
   * @param userId id of the user whose profile is being viewed.
   */
  void viewProfile(int userId);

  /**
   * applies profile changes (name, email, oauth) to the database.
   *
   * @param updatedUser new user data built from the edited form.
   */
  void updateProfile(IBaseUser updatedUser);

  /**
   * permanently deletes the signed-in user's account.
   *
   * @param userId id of the user to delete.
   */
  void deleteAccount(int userId);
}
