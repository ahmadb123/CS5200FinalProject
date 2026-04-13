package controller.uiFeatures;

/**
 * Auth features interface — the 3 user-authentication actions the UI can invoke.
 * register, login, and logout. the controller implements these and pushes results
 * back to the view via display callbacks.
 */
public interface AuthFeatures {

  /**
   * registers a new user account.
   *
   * @param name  user's display name.
   * @param email user's email (must be unique).
   * @param oauth oauth provider label (e.g. "google", "local").
   */
  void register(String name, String email, String oauth);

  /**
   * authenticates a user by email and pushes the result to the view.
   *
   * @param email email to look up.
   */
  void login(String email);

  /**
   * ends the current session for a given user.
   *
   * @param userId id of the user logging out.
   */
  void logout(int userId);
}
