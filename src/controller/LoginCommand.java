package controller;

import java.sql.SQLException;
import model.user.IBaseUser;
import model.user.IUserModel;

/**
 * Command that authenticates a user by email. execute() returns the matching
 * user (StandardUser or AdminUser) or null if no account exists.
 */
public class LoginCommand implements Icommand<IBaseUser> {
  private final IUserModel userModel;
  private final String email;

  public LoginCommand(IUserModel userModel, String email) {
    this.userModel = userModel;
    this.email = email;
  }

  @Override
  public IBaseUser execute() throws SQLException {
    return userModel.login(email);
  }
}
