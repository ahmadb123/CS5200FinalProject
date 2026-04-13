package controller;

import java.sql.SQLException;
import model.user.IAdminModel;
import model.user.IBaseUser;

/**
 * Admin command that promotes a regular user to admin. execute() flips the
 * is_admin flag to true and returns the refreshed user — which will now be
 * an AdminUser instance thanks to the factory in BaseUserAbstract.of(...).
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
