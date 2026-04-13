package controller.uiFeatures;

import controller.CalculateOrderTotalCommand;
import controller.CancelOrderCommand;
import controller.CountUsersCommand;
import controller.DeleteAccountCommand;
import controller.DeleteAnyUserCommand;
import controller.DeleteOrderCommand;
import controller.DemoteFromAdminCommand;
import controller.FindOrderByIdCommand;
import controller.LoginCommand;
import controller.LogoutCommand;
import controller.PlaceOrderCommand;
import controller.PromoteToAdminCommand;
import controller.RegisterCommand;
import controller.UpdateOrderStatusCommand;
import controller.UpdateProfileCommand;
import controller.ViewAllOrdersCommand;
import controller.ViewAllUsersCommand;
import controller.ViewMyOrdersCommand;
import controller.ViewProfileCommand;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import model.order.IOrderModel;
import model.order.Order;
import model.user.IAdminModel;
import model.user.IBaseUser;
import model.user.IUserModel;
import view.IguiViewWorldBuy;

/**
 * Concrete controller that implements UiFeatures and pushes updates to a
 * Swing view via the observer pattern. each feature method:
 *   1. validates inputs where applicable,
 *   2. executes the matching command,
 *   3. pushes the typed result to the view (displayUser / displayOrders / etc.),
 *   4. or pushes an error message via displayError on failure.
 * holds three model references — IUserModel for regular ops, IAdminModel for
 * admin ops (same concrete instance), and IOrderModel for orders. the view
 * reference is set after construction via setView(), which also hands this
 * controller to the view so it can wire button listeners.
 */
public class GuiController implements UiFeatures {

  private final IUserModel userModel;
  private final IAdminModel adminModel;
  private final IOrderModel orderModel;
  private IguiViewWorldBuy view;

  public GuiController(IUserModel userModel, IAdminModel adminModel, IOrderModel orderModel) {
    this.userModel = userModel;
    this.adminModel = adminModel;
    this.orderModel = orderModel;
  }

  public void setView(IguiViewWorldBuy view) {
    this.view = view;
    view.addFeatures(this);
  }

  // =====================================================
  // AuthFeatures
  // =====================================================

  @Override
  public void register(String name, String email, String oauth) {
    if (isBlank(name) || isBlank(email) || isBlank(oauth)) {
      view.displayError("Name, email, and OAuth provider are required");
      return;
    }
    try {
      IBaseUser user = new RegisterCommand(userModel, name, email, oauth).execute();
      if (user != null) {
        view.displayUser(user);
        view.displayMessage("Registered: " + user.getName());
      } else {
        view.displayError("Registration failed");
      }
    } catch (SQLException e) {
      if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
        view.displayError("Email already in use");
      } else {
        view.displayError("Registration error: " + e.getMessage());
      }
    }
  }

  @Override
  public void login(String email) {
    if (isBlank(email)) {
      view.displayError("Email is required");
      return;
    }
    try {
      IBaseUser user = new LoginCommand(userModel, email).execute();
      if (user != null) {
        view.displayUser(user);
      } else {
        view.displayError("No account found for " + email);
      }
    } catch (SQLException e) {
      view.displayError("Login error: " + e.getMessage());
    }
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  @Override
  public void logout(int userId) {
    try {
      new LogoutCommand(userModel, userId).execute();
      view.displayMessage("Logged out");
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  // =====================================================
  // ProfileFeatures
  // =====================================================

  @Override
  public void viewProfile(int userId) {
    try {
      IBaseUser user = new ViewProfileCommand(userModel, userId).execute();
      if (user != null) {
        view.displayUser(user);
      } else {
        view.displayError("User not found");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void updateProfile(IBaseUser updatedUser) {
    try {
      IBaseUser refreshed = new UpdateProfileCommand(userModel, updatedUser).execute();
      if (refreshed != null) {
        view.displayUser(refreshed);
        view.displayMessage("Profile updated");
      } else {
        view.displayError("Profile update failed");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void deleteAccount(int userId) {
    try {
      boolean deleted = new DeleteAccountCommand(userModel, userId).execute();
      if (deleted) {
        view.displayMessage("Account deleted");
      } else {
        view.displayError("Account not found");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  // =====================================================
  // OrderFeatures
  // =====================================================

  @Override
  public void placeOrder(int userId, String shipping, String payment) {
    try {
      Order order = new PlaceOrderCommand(orderModel, userId, shipping, payment).execute();
      if (order != null) {
        view.displayOrder(order);
        view.displayMessage("Order placed: #" + order.getOrderId());
      } else {
        view.displayError("Failed to place order");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void findOrdersByUser(int userId) {
    try {
      List<Order> orders = new ViewMyOrdersCommand(orderModel, userId).execute();
      view.displayOrders(orders);
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void findAllOrders() {
    try {
      List<Order> orders = new ViewAllOrdersCommand(orderModel).execute();
      view.displayOrders(orders);
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void findOrderById(int orderId) {
    try {
      Order order = new FindOrderByIdCommand(orderModel, orderId).execute();
      if (order != null) {
        view.displayOrder(order);
      } else {
        view.displayError("Order #" + orderId + " not found");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void cancelOrder(int orderId) {
    try {
      boolean cancelled = new CancelOrderCommand(orderModel, orderId).execute();
      if (cancelled) {
        view.displayMessage("Order #" + orderId + " cancelled");
      } else {
        view.displayError("Could not cancel order #" + orderId);
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void updateOrdersStatus(int orderId, String newStatus) {
    try {
      boolean updated =
          new UpdateOrderStatusCommand(orderModel, orderId, newStatus).execute();
      if (updated) {
        view.displayMessage("Order #" + orderId + " status: " + newStatus);
      } else {
        view.displayError("Could not update order #" + orderId);
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void deleteOrder(int orderId) {
    try {
      boolean deleted = new DeleteOrderCommand(orderModel, orderId).execute();
      if (deleted) {
        view.displayMessage("Order #" + orderId + " deleted");
      } else {
        view.displayError("Could not delete order #" + orderId);
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void calculateOrderTotal(int orderId) {
    try {
      BigDecimal total = new CalculateOrderTotalCommand(orderModel, orderId).execute();
      view.displayMessage("Order #" + orderId + " total: $" + total);
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  // =====================================================
  // AdminFeatures
  // =====================================================

  @Override
  public void viewAllUsers() {
    try {
      List<IBaseUser> users = new ViewAllUsersCommand(adminModel).execute();
      view.displayUsers(users);
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void promoteToAdmin(int userId) {
    try {
      IBaseUser user = new PromoteToAdminCommand(adminModel, userId).execute();
      if (user != null) {
        view.displayUser(user);
        view.displayMessage(user.getName() + " promoted to admin");
      } else {
        view.displayError("User not found");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void demoteFromAdmin(int userId) {
    try {
      IBaseUser user = new DemoteFromAdminCommand(adminModel, userId).execute();
      if (user != null) {
        view.displayUser(user);
        view.displayMessage(user.getName() + " demoted from admin");
      } else {
        view.displayError("User not found");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void deleteAnyUser(int userId) {
    try {
      boolean deleted = new DeleteAnyUserCommand(adminModel, userId).execute();
      if (deleted) {
        view.displayMessage("User #" + userId + " deleted");
      } else {
        view.displayError("User #" + userId + " not found");
      }
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void countUsers() {
    try {
      int count = new CountUsersCommand(adminModel).execute();
      view.displayMessage("Total users: " + count);
    } catch (SQLException e) {
      view.displayError(e.getMessage());
    }
  }
}
