package model.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.DBConnection;

/**
 * JDBC implementation of the user and admin model. Implements IAdminModel
 * (which extends IUserModel), so one instance satisfies both interfaces.
 * The controller hands it out as IUserModel or IAdminModel based on the
 * caller's role. All SQL goes through stored procedures (register_user,
 * find_user_by_email, promote_user_to_admin, etc.). Row mapping calls
 * BaseUserAbstract.of(...) to return the correct subclass.
 */
public class UserModel implements IAdminModel {
  private final DBConnection database;

  /**
   * @param db shared DB connection factory injected by Main.
   */
  public UserModel(DBConnection db) {
    this.database = db;
  }

  // =====================================================
  // IUserModel methods
  // =====================================================

  @Override
  public IBaseUser register(String name, String email, String oauth) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL register_user(?, ?, ?)}")) {

      cs.setString(1, name);
      cs.setString(2, email);
      cs.setString(3, oauth);

      if (cs.execute()) {
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return mapRow(rs);
          }
        }
      }
    }
    return null;
  }

  @Override
  public IBaseUser login(String email) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_user_by_email(?)}")) {

      cs.setString(1, email);

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    }
    return null;
  }

  @Override
  public void logout(int userId) {
    // session-only; no database work
  }

  @Override
  public IBaseUser viewProfile(int userId) throws SQLException {
    return findUserById(userId);
  }

  @Override
  public IBaseUser updateProfile(IBaseUser updatedUser) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL update_user_profile(?, ?, ?, ?)}")) {

      cs.setInt(1, updatedUser.getUserId());
      cs.setString(2, updatedUser.getName());
      cs.setString(3, updatedUser.getEmail());
      cs.setString(4, updatedUser.getOauthProvider());

      int rows = cs.executeUpdate();
      if (rows > 0) {
        return updatedUser;
      }
    }
    return null;
  }

  @Override
  public boolean deleteAccount(int userId) throws SQLException {
    return deleteAnyUser(userId);
  }

  // =====================================================
  // IAdminModel methods
  // =====================================================

  @Override
  public List<IBaseUser> viewAllUsers() throws SQLException {
    List<IBaseUser> users = new ArrayList<>();

    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_all_users()}")) {

      try (ResultSet rs = cs.executeQuery()) {
        while (rs.next()) {
          users.add(mapRow(rs));
        }
      }
    }
    return users;
  }

  @Override
  public IBaseUser findUserById(int userId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL find_user_by_id(?)}")) {

      cs.setInt(1, userId);

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    }
    return null;
  }

  @Override
  public IBaseUser promoteToAdmin(int userId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL promote_user_to_admin(?)}")) {

      cs.setInt(1, userId);

      if (cs.execute()) {
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return mapRow(rs);
          }
        }
      }
    }
    return null;
  }

  @Override
  public IBaseUser demoteFromAdmin(int userId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL demote_user_from_admin(?)}")) {

      cs.setInt(1, userId);

      if (cs.execute()) {
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return mapRow(rs);
          }
        }
      }
    }
    return null;
  }

  @Override
  public boolean deleteAnyUser(int userId) throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL delete_user(?)}")) {

      cs.setInt(1, userId);
      return cs.executeUpdate() > 0;
    }
  }

  @Override
  public int countUsers() throws SQLException {
    try (Connection conn = database.getConnection();
         CallableStatement cs = conn.prepareCall("{CALL count_users()}")) {

      try (ResultSet rs = cs.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("total");
        }
      }
    }
    return 0;
  }

  // =====================================================
  // Row mapping helper
  // =====================================================

  private IBaseUser mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("created_at");
    return BaseUserAbstract.of(
        rs.getInt("user_id"),
        rs.getString("name"),
        rs.getString("email"),
        rs.getString("oauth_provider"),
        rs.getBoolean("is_admin"),
        ts != null ? ts.toLocalDateTime() : null
    );
  }
}
