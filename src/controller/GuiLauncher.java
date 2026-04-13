package controller;

import controller.uiFeatures.GuiController;
import javax.swing.SwingUtilities;
import model.DBConnection;
import model.order.OrderModel;
import model.user.UserModel;
import view.WorldBuyGuiView;

/**
 * GUI launcher. builds the Swing stack (DB connection, models, feature controller, view)
 * and shows the main window on the Swing event dispatch thread.
 * selected by Main when no --mode flag is given or when --mode gui is passed.
 */
public class GuiLauncher implements IWorldBuyController {

  /**
   * wires and starts the GUI. creates models, the feature controller,
   * and the main view, then makes the JFrame visible.
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
