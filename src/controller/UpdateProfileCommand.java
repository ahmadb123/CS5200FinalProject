package controller;

import java.sql.SQLException;
import model.user.IBaseUser;
import model.user.IUserModel;

/**
 * Command that persists edited profile fields. constructed with a fully-built
 * IBaseUser (usually created via BaseUserAbstract.of(...) by the caller).
 * execute() returns the refreshed user on success.
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
