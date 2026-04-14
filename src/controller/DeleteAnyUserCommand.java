package controller;

import java.sql.SQLException;
import model.user.IAdminModel;

/**
 * Admin-only: permanently deletes any user by id. The foreign key on
 * orders cascades so the user's orders are removed as well. execute()
 * returns true if a row was deleted.
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
