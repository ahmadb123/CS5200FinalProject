package controller;

import java.sql.SQLException;
import model.user.IUserModel;

/**
 * Deletes a user's own account. The foreign keys on orders cascade, so
 * the user's orders disappear with them. execute() returns true when a
 * row was deleted.
 */
public class DeleteAccountCommand implements Icommand<Boolean>{
  private final IUserModel userModel;
  private final int userId;

  public DeleteAccountCommand(IUserModel userModel, int userId){
    this.userModel = userModel;
    this.userId = userId;
  }

  @Override
  public Boolean execute() throws SQLException {
    return userModel.deleteAccount(userId);
  }
}
