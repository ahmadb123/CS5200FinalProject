package controller;

import java.sql.SQLException;
import model.user.IAdminModel;

/**
 * Admin-only: returns the total user count in the system.
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
