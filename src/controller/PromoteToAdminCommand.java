package controller;

import java.sql.SQLException;
import model.user.IAdminModel;
import model.user.IBaseUser;

/**
 * Admin-only: promotes a regular user to admin. execute() sets the
 * is_admin flag to true and returns the refreshed user. The returned
 * object will be an AdminUser instance, because BaseUserAbstract.of(...)
 * picks the subclass based on the new flag.
 */
public class PromoteToAdminCommand implements Icommand<IBaseUser> {

  private final IAdminModel adminModel;
  private final int userId;

  public PromoteToAdminCommand(IAdminModel adminModel, int userId) {
    this.adminModel = adminModel;
    this.userId = userId;
  }

  @Override
  public IBaseUser execute() throws SQLException {
    return adminModel.promoteToAdmin(userId);
  }
}
