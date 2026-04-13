package controller;

import model.DBConnection;
import model.emailNotification.EmailNotificationModel;
import model.exchangeRate.ExchangeRateModel;
import model.order.OrderModel;
import model.order.orderItem.OrderItemModel;
import model.orderLog.OrderLogModel;
import model.payment.PaymentModel;
import model.product.ProductModel;
import model.shipment.ShipmentModel;
import model.user.UserModel;
import view.ConsoleView;

/**
 * CLI launcher. wires the full set of models (all 9 entities) and hands them
 * to the text-based console view. selected by Main when --mode cli is passed.
 * unlike the GUI which is scoped to User + Order, the CLI exposes every entity
 * so graders / developers can exercise the full model layer from a terminal.
 */
public class CliLauncher implements IWorldBuyController {

  /**
   * wires all 9 models and starts the console view's menu loop.
   */
  @Override
  public void run() {
    DBConnection db = new DBConnection();
    UserModel userModel = new UserModel(db);
    ProductModel productModel = new ProductModel(db);
    OrderModel orderModel = new OrderModel(db);
    OrderItemModel itemModel = new OrderItemModel(db);
    PaymentModel paymentModel = new PaymentModel(db);
    ShipmentModel shipmentModel = new ShipmentModel(db);
    OrderLogModel logModel = new OrderLogModel(db);
    EmailNotificationModel notifModel = new EmailNotificationModel(db);
    ExchangeRateModel rateModel = new ExchangeRateModel(db);

    ConsoleView view = new ConsoleView(
        userModel, productModel, orderModel, itemModel,
        paymentModel, shipmentModel, logModel, notifModel, rateModel);

    view.run();
  }
}
