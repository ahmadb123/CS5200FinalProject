package controller;

import java.sql.SQLException;
import model.user.IUserModel;

/**
 * Ends a user session. There is no DB work here because logout is just
 * client-side session state, so execute() always returns null.
 */
public class LogoutCommand implements Icommand<Void>{
  private final IUserModel userModel;
  private final int userId;

  public LogoutCommand(IUserModel userModel, int userId){
    this.userModel = userModel;
    this.userId = userId;
  }
  @Override
  public Void execute() throws SQLException {
    userModel.logout(userId);
    return null;
  }
}
