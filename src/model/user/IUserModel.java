package model.user;

import java.sql.SQLException;

public interface IUserModel {
  IBaseUser register(String name, String email, String oauth) throws SQLException;
  IBaseUser login(String email) throws SQLException;
  void logout(int userId);
  IBaseUser viewProfile(int userId) throws SQLException;
  IBaseUser updateProfile(IBaseUser updatedUser) throws SQLException;
  boolean deleteAccount(int userId) throws SQLException;
}
