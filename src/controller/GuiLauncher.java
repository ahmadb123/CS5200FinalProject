package controller;

import controller.uiFeatures.GuiController;
import javax.swing.SwingUtilities;
import model.DBConnection;
import model.order.OrderModel;
import model.user.UserModel;
import view.WorldBuyGuiView;

/**
 * GUI launcher. Builds the Swing stack (DB connection, models, feature
 * controller, view) and shows the main window on the Swing event
 * dispatch thread. Picked by Main when no --mode flag is given or when
 * --mode gui is passed.
 */
public class GuiLauncher implements IWorldBuyController {

  /**
   * Starts the GUI by creating the models, the feature controller, and
   * the main view, then making the JFrame visible.
   */
  @Override
  public void run() {
    SwingUtilities.invokeLater(() -> {
      DBConnection db = new DBConnection();
      UserModel userModel = new UserModel(db);
      OrderModel orderModel = new OrderModel(db);

      GuiController controller = new GuiController(userModel, userModel, orderModel);
      WorldBuyGuiView view = new WorldBuyGuiView();
      controller.setView(view);
      view.setVisible(true);
    });
  }
}
