package controller;

import java.sql.SQLException;
import model.user.IBaseUser;
import model.user.IUserModel;

/**
 * Saves edited profile fields back to the DB. Takes a fully built
 * IBaseUser (normally created by the caller with
 * BaseUserAbstract.of(...)). execute() returns the refreshed user on
 * success.
 */
public class UpdateProfileCommand implements Icommand<IBaseUser> {
  private final IUserModel userModel;
  private final IBaseUser updateUser;

  public UpdateProfileCommand(IUserModel userModel, IBaseUser updateUser){
    this.userModel = userModel;
    this.updateUser = updateUser;
  }

  @Override
  public IBaseUser execute() throws SQLException {
    return userModel.updateProfile(updateUser);
  }
}
