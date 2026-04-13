package controller;

import java.sql.SQLException;
import model.user.IAdminModel;

/**
 * Admin command that hard-deletes any user by id. cascades to that user's
 * orders via FK. execute() returns true if a row was deleted.
 */
public class DeleteAnyUserCommand implements Icommand<Boolean> {

  private final IAdminModel adminModel;
  private final int userId;

  public DeleteAnyUserCommand(IAdminModel adminModel, int userId) {
    this.adminModel = adminModel;
    this.userId = userId;
  }

  @Override
  public Boolean execute() throws SQLException {
    return adminModel.deleteAnyUser(userId);
  }
}
