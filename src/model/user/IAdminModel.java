package model.user;

import java.sql.SQLException;
import java.util.List;

public interface IAdminModel extends IUserModel {

  List<IBaseUser> viewAllUsers() throws SQLException;

  IBaseUser findUserById(int userId) throws SQLException;

  IBaseUser promoteToAdmin(int userId) throws SQLException;

  IBaseUser demoteFromAdmin(int userId) throws SQLException;

  boolean deleteAnyUser(int userId) throws SQLException;

  int countUsers() throws SQLException;
}
