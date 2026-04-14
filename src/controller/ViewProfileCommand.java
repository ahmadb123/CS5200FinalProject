package controller;

import java.sql.SQLException;
import model.user.IBaseUser;
import model.user.IUserModel;

/**
 * Looks up a user by id. execute() returns the matching user, or null
 * if there is no such user.
 */
public class ViewProfileCommand implements Icommand<IBaseUser> {
  private final IUserModel userModel;
  private final int userId;

  public ViewProfileCommand(IUserModel userModel, int userId){
    this.userModel = userModel;
    this.userId = userId;
  }

  @Override
  public IBaseUser execute() throws SQLException {
    return userModel.viewProfile(userId);
  }
}
