package controller;

import java.sql.SQLException;
import model.user.IBaseUser;
import model.user.IUserModel;
import model.user.UserModel;

/**
 * Registers a new user. Takes the user model and the three registration
 * fields (name, email, and oauth provider). execute() inserts the row
 * and returns the new user with its DB-assigned id.
 */
public class RegisterCommand implements Icommand <IBaseUser> {
  private final IUserModel userModel;
  private final String name;
  private final String email;
  private final String oauth;

  public RegisterCommand(IUserModel userModel, String name, String email, String oauth){
    this.userModel = userModel;
    this.name = name;
    this.email = email;
    this.oauth = oauth;
  }
  @Override
  public IBaseUser execute() throws SQLException {
    return userModel.register(name, email, oauth);
  }
}
