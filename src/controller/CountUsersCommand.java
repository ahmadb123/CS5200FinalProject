package controller;

import java.sql.SQLException;
import model.user.IAdminModel;

/**
 * Admin command that returns the total number of users in the system.
 */
public class CountUsersCommand implements Icommand<Integer> {

  private final IAdminModel adminModel;

  public CountUsersCommand(IAdminModel adminModel) {
    this.adminModel = adminModel;
  }

  @Override
  public Integer execute() throws SQLException {
    return adminModel.countUsers();
  }
}
