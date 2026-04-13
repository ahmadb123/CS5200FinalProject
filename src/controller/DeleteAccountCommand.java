package controller;

import java.sql.SQLException;
import model.user.IUserModel;

/**
 * Command that deletes a user's own account. cascades to the user's orders
 * via FK. execute() returns true if a row was deleted.
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
