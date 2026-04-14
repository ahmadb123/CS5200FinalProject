package controller;

import java.sql.SQLException;
import model.user.IAdminModel;
import model.user.IBaseUser;

/**
 * Admin-only: demotes an admin back to a regular user. execute() sets
 * is_admin to false and returns the refreshed user. The returned object
 * will be a StandardUser, thanks to the factory.
 */
public class DemoteFromAdminCommand implements Icommand<IBaseUser> {

  private final IAdminModel adminModel;
  private final int userId;

  public DemoteFromAdminCommand(IAdminModel adminModel, int userId) {
    this.adminModel = adminModel;
    this.userId = userId;
  }

  @Override
  public IBaseUser execute() throws SQLException {
    return adminModel.demoteFromAdmin(userId);
  }
}
