package controller.uiFeatures;

/**
 * Authentication actions the UI can call: register, login, and logout.
 * The controller implements these and pushes results back to the view
 * through its display methods.
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
