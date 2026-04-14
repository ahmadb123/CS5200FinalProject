package view;


import java.util.List;
import model.order.Order;
import model.user.IBaseUser;

/**
 * Base view interface. Every WorldBuy view (GUI or text) implements this
 * and the controller uses its methods to push data and messages into the
 * UI. There are two groups of methods: status methods (displayMessage,
 * displayError) and typed data displays (displayUser, displayUsers,
 * displayOrder, displayOrders).
 */
public interface IWorldBuyView {

  /**
   * displays a general success / informational message to the user.
   *
   * @param message text to show.
   */
  void displayMessage(String message);

  /**
   * displays an error message. typically rendered in red or prefixed with "ERROR:".
   *
   * @param message error description.
   */
  void displayError(String message);

  /**
   * shows a single user's details (login, profile view, profile update).
   *
   * @param user user to display; may trigger navigation on first login.
   */
  void displayUser(IBaseUser user);

  /**
   * shows a list of users (admin dashboard).
   *
   * @param users users to display. may be empty.
   */
  void displayUsers(List<IBaseUser> users);

  /**
   * shows details of a single order.
   *
   * @param order order to display.
   */
  void displayOrder(Order order);

  /**
   * shows a list of orders (user history or admin all-orders view).
   *
   * @param orders orders to display. may be empty.
   */
  void displayOrders(List<Order> orders);
}
