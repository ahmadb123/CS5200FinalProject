package controller;

import java.sql.SQLException;
import model.user.IUserModel;

/**
 * Command that ends a user session. no DB operation happens — logout is
 * purely session state on the client. execute() returns Void (always null).
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
