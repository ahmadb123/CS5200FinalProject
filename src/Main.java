import model.DBConnection;
import model.user.IBaseUser;
import model.user.UserModel;
import model.order.Order;
import model.order.OrderModel;
import model.payment.Payment;
import model.payment.PaymentModel;

public class Main {
  public static void main(String[] args) {
    DBConnection db = new DBConnection();
    UserModel userModel = new UserModel(db);
    OrderModel orderModel = new OrderModel(db);
    PaymentModel paymentModel = new PaymentModel(db);

    try {
      IBaseUser ahmad = userModel.register("Ahmad", "ahmad@example.com", "local");
      Order order = orderModel.placeOrder(ahmad.getUserId(), "standard", "credit");

      Payment payment = paymentModel.createPayment(order.getOrderId(), "credit");
      System.out.println("Created: id=" + payment.getPaymentId()
          + " status=" + payment.getPaymentStatus()
          + " paidAt=" + payment.getPaidAt());

      paymentModel.markAsPaid(payment.getPaymentId());

      Payment paid = paymentModel.findPaymentByOrderId(order.getOrderId());
      System.out.println("After markAsPaid: status=" + paid.getPaymentStatus()
          + " paidAt=" + paid.getPaidAt());

      orderModel.deleteOrder(order.getOrderId());
      userModel.deleteAnyUser(ahmad.getUserId());
      System.out.println("Cleanup done.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}