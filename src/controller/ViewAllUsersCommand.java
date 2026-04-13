package controller;

import java.sql.SQLException;
import java.util.List;
import model.user.IAdminModel;
import model.user.IBaseUser;

/**
 * Admin command that loads every user in the system. execute() returns
 * the full list (each user is a StandardUser or AdminUser via factory).
 */
public class ViewAllUsersCommand implements Icommand<List<IBaseUser>> {

  private final IAdminModel adminModel;

  public ViewAllUsersCommand(IAdminModel adminModel) {
    this.adminModel = adminModel;
  }

  @Override
  public List<IBaseUser> execute() throws SQLException {
    return adminModel.viewAllUsers();
  }
}
