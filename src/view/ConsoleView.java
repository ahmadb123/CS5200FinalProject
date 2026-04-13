package view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import model.emailNotification.EmailNotification;
import model.emailNotification.EmailNotificationModel;
import model.exchangeRate.ExchangeRate;
import model.exchangeRate.ExchangeRateModel;
import model.order.Order;
import model.order.OrderModel;
import model.order.orderItem.OrderItem;
import model.order.orderItem.OrderItemModel;
import model.orderLog.OrderLog;
import model.orderLog.OrderLogModel;
import model.payment.Payment;
import model.payment.PaymentModel;
import model.product.Product;
import model.product.ProductModel;
import model.shipment.Shipment;
import model.shipment.ShipmentModel;
import model.user.BaseUserAbstract;
import model.user.IBaseUser;
import model.user.UserModel;

/**
 * Text-based console view for WorldBuy.
 * drives the application via nested menu loops over System.in / System.out.
 * exposes every CRUD operation for all 9 entities so graders can exercise
 * the full model layer from a terminal. role-aware: regular users see the
 * user menu, admins see the admin menu with extra user-management options.
 * directly holds model references (no features indirection) — simpler for
 * breadth / debugging than the Swing GUI which goes through UiFeatures.
 */
public class ConsoleView {

  private final Scanner in = new Scanner(System.in);
  private final UserModel userModel;
  private final ProductModel productModel;
  private final OrderModel orderModel;
  private final OrderItemModel itemModel;
  private final PaymentModel paymentModel;
  private final ShipmentModel shipmentModel;
  private final OrderLogModel logModel;
  private final EmailNotificationModel notifModel;
  private final ExchangeRateModel rateModel;

  private IBaseUser currentUser;
  private boolean running = true;

  /**
   * constructs the console view with all 9 model references.
   *
   * @param userModel     user + admin operations.
   * @param productModel  product catalog operations.
   * @param orderModel    order operations.
   * @param itemModel     order-items (cart) operations.
   * @param paymentModel  payment operations.
   * @param shipmentModel shipment operations.
   * @param logModel      order log operations.
   * @param notifModel    email notification operations.
   * @param rateModel     exchange rate operations.
   */
  public ConsoleView(UserModel userModel, ProductModel productModel, OrderModel orderModel,
                     OrderItemModel itemModel, PaymentModel paymentModel,
                     ShipmentModel shipmentModel, OrderLogModel logModel,
                     EmailNotificationModel notifModel, ExchangeRateModel rateModel) {
    this.userModel = userModel;
    this.productModel = productModel;
    this.orderModel = orderModel;
    this.itemModel = itemModel;
    this.paymentModel = paymentModel;
    this.shipmentModel = shipmentModel;
    this.logModel = logModel;
    this.notifModel = notifModel;
    this.rateModel = rateModel;
  }

  /**
   * main menu loop. runs until the user chooses exit. dispatches to
   * mainMenu / userMenu / adminMenu based on the current login state.
   */
  public void run() {
    System.out.println("========================================");
    System.out.println("      WorldBuy CLI");
    System.out.println("========================================");
    while (running) {
      try {
        if (currentUser == null) {
          mainMenu();
        } else if (currentUser.isAdmin()) {
          adminMenu();
        } else {
          userMenu();
        }
      } catch (SQLException e) {
        System.out.println("[DB ERROR] " + e.getMessage());
      } catch (Exception e) {
        System.out.println("[ERROR] " + e.getMessage());
      }
    }
    System.out.println("Goodbye.");
  }

  // =====================================================
  // Top-level menus
  // =====================================================

  private void mainMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Main Menu ---");
    System.out.println("1. Login");
    System.out.println("2. Register");
    System.out.println("0. Exit");
    switch (prompt("Choose: ")) {
      case "1": doLogin(); break;
      case "2": doRegister(); break;
      case "0": running = false; break;
      default: System.out.println("Invalid choice.");
    }
  }

  private void userMenu() throws SQLException {
    System.out.println();
    System.out.println("--- User Menu (" + currentUser.getName() + ") ---");
    System.out.println(" 1. Profile");
    System.out.println(" 2. Products");
    System.out.println(" 3. Orders");
    System.out.println(" 4. Cart (Order Items)");
    System.out.println(" 5. Payments");
    System.out.println(" 6. Shipments");
    System.out.println(" 7. Order Logs");
    System.out.println(" 8. Notifications");
    System.out.println(" 9. Currency / Exchange Rates");
    System.out.println(" 0. Logout");
    switch (prompt("Choose: ")) {
      case "1": profileMenu(); break;
      case "2": productsMenu(); break;
      case "3": ordersMenu(false); break;
      case "4": cartMenu(); break;
      case "5": paymentsMenu(); break;
      case "6": shipmentsMenu(); break;
      case "7": orderLogsMenu(); break;
      case "8": notificationsMenu(); break;
      case "9": currencyMenu(); break;
      case "0": doLogout(); break;
      default: System.out.println("Invalid choice.");
    }
  }

  private void adminMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Admin Menu (" + currentUser.getName() + ") ---");
    System.out.println(" 1. Profile");
    System.out.println(" 2. Products");
    System.out.println(" 3. Orders");
    System.out.println(" 4. Cart (Order Items)");
    System.out.println(" 5. Payments");
    System.out.println(" 6. Shipments");
    System.out.println(" 7. Order Logs");
    System.out.println(" 8. Notifications");
    System.out.println(" 9. Currency / Exchange Rates");
    System.out.println("10. User Management");
    System.out.println(" 0. Logout");
    switch (prompt("Choose: ")) {
      case "1": profileMenu(); break;
      case "2": productsMenu(); break;
      case "3": ordersMenu(true); break;
      case "4": cartMenu(); break;
      case "5": paymentsMenu(); break;
      case "6": shipmentsMenu(); break;
      case "7": orderLogsMenu(); break;
      case "8": notificationsMenu(); break;
      case "9": currencyMenu(); break;
      case "10": userManagementMenu(); break;
      case "0": doLogout(); break;
      default: System.out.println("Invalid choice.");
    }
  }

  // =====================================================
  // Auth
  // =====================================================

  private void doLogin() throws SQLException {
    String email = prompt("Email: ");
    IBaseUser user = userModel.login(email);
    if (user == null) {
      System.out.println("No account found.");
      return;
    }
    currentUser = user;
    System.out.println("Logged in as " + user.getName() + (user.isAdmin() ? " (admin)" : ""));
  }

  private void doRegister() throws SQLException {
    String name = prompt("Name: ");
    String email = prompt("Email: ");
    String oauth = prompt("OAuth provider (google/local): ");
    IBaseUser user = userModel.register(name, email, oauth);
    if (user != null) {
      currentUser = user;
      System.out.println("Registered and logged in: " + user.getName());
    } else {
      System.out.println("Registration failed.");
    }
  }

  private void doLogout() {
    if (currentUser != null) {
      userModel.logout(currentUser.getUserId());
    }
    currentUser = null;
    System.out.println("Logged out.");
  }

  // =====================================================
  // Profile
  // =====================================================

  private void profileMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Profile ---");
    System.out.println("1. View my profile");
    System.out.println("2. Update my profile");
    System.out.println("3. Delete my account");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        IBaseUser u = userModel.viewProfile(currentUser.getUserId());
        if (u == null) {
          System.out.println("User not found.");
        } else {
          printUser(u);
        }
        break;
      }
      case "2": {
        String name = prompt("New name [" + currentUser.getName() + "]: ");
        String email = prompt("New email [" + currentUser.getEmail() + "]: ");
        String oauth = prompt("New OAuth [" + currentUser.getOauthProvider() + "]: ");
        IBaseUser updated = BaseUserAbstract.of(
            currentUser.getUserId(),
            name.isEmpty() ? currentUser.getName() : name,
            email.isEmpty() ? currentUser.getEmail() : email,
            oauth.isEmpty() ? currentUser.getOauthProvider() : oauth,
            currentUser.isAdmin(),
            currentUser.getCreatedAt());
        IBaseUser result = userModel.updateProfile(updated);
        if (result != null) {
          currentUser = result;
          System.out.println("Profile updated.");
          printUser(result);
        } else {
          System.out.println("Update failed.");
        }
        break;
      }
      case "3": {
        String c = prompt("Type 'yes' to confirm: ");
        if ("yes".equalsIgnoreCase(c)) {
          boolean deleted = userModel.deleteAccount(currentUser.getUserId());
          if (deleted) {
            System.out.println("Account deleted.");
            currentUser = null;
          } else {
            System.out.println("Delete failed.");
          }
        }
        break;
      }
      default:
    }
  }

  // =====================================================
  // Products
  // =====================================================

  private void productsMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Products ---");
    System.out.println("1. Browse all");
    System.out.println("2. Search by name");
    System.out.println("3. View details");
    System.out.println("4. Add / submit new product");
    System.out.println("5. Update product");
    System.out.println("6. Update price");
    System.out.println("7. Delete product");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        List<Product> all = productModel.findAllProducts();
        printProducts(all);
        break;
      }
      case "2": {
        String kw = prompt("Keyword: ");
        printProducts(productModel.searchByName(kw));
        break;
      }
      case "3": {
        int id = promptInt("Product ID: ");
        Product p = productModel.findProductById(id);
        if (p == null) {
          System.out.println("Not found.");
        } else {
          printProduct(p);
        }
        break;
      }
      case "4": {
        String url = prompt("URL: ");
        String name = prompt("Name: ");
        String spec = prompt("Specification: ");
        BigDecimal price = promptDecimal("Unit price: ");
        Product p = productModel.addProduct(url, name, spec, price);
        if (p != null) {
          System.out.println("Created product #" + p.getProductId());
        } else {
          System.out.println("Failed.");
        }
        break;
      }
      case "5": {
        int id = promptInt("Product ID: ");
        Product existing = productModel.findProductById(id);
        if (existing == null) {
          System.out.println("Not found.");
          break;
        }
        String url = prompt("URL [" + existing.getProductUrl() + "]: ");
        String name = prompt("Name [" + existing.getProductName() + "]: ");
        String spec = prompt("Spec [" + existing.getSpecification() + "]: ");
        String priceStr = prompt("Unit price [" + existing.getUnitPrice() + "]: ");
        Product updated = existing.toBuilder()
            .withProductUrl(url.isEmpty() ? existing.getProductUrl() : url)
            .withProductName(name.isEmpty() ? existing.getProductName() : name)
            .withSpecification(spec.isEmpty() ? existing.getSpecification() : spec)
            .withUnitPrice(priceStr.isEmpty() ? existing.getUnitPrice() : new BigDecimal(priceStr))
            .build();
        boolean ok = productModel.updateProduct(updated);
        System.out.println(ok ? "Updated." : "Update failed.");
        break;
      }
      case "6": {
        int id = promptInt("Product ID: ");
        BigDecimal price = promptDecimal("New price: ");
        boolean ok = productModel.updatePrice(id, price);
        System.out.println(ok ? "Price updated." : "Update failed.");
        break;
      }
      case "7": {
        int id = promptInt("Product ID: ");
        boolean ok = productModel.deleteProduct(id);
        System.out.println(ok ? "Deleted." : "Delete failed.");
        break;
      }
      default:
    }
  }

  // =====================================================
  // Orders
  // =====================================================

  private void ordersMenu(boolean isAdmin) throws SQLException {
    System.out.println();
    System.out.println("--- Orders ---");
    System.out.println("1. Place new order");
    System.out.println("2. View my orders");
    System.out.println("3. View order details");
    System.out.println("4. Cancel order");
    System.out.println("5. Calculate order total (SQL function)");
    if (isAdmin) {
      System.out.println("6. View all orders");
      System.out.println("7. Find orders by status");
      System.out.println("8. Update order status");
      System.out.println("9. Delete order");
    }
    System.out.println("0. Back");
    String choice = prompt("Choose: ");
    switch (choice) {
      case "1": {
        String shipping = prompt("Shipping method: ");
        String payment = prompt("Payment method: ");
        Order o = orderModel.placeOrder(currentUser.getUserId(), shipping, payment);
        if (o != null) {
          System.out.println("Placed order #" + o.getOrderId());
          printOrder(o);
        }
        break;
      }
      case "2": {
        printOrders(orderModel.findOrdersByUser(currentUser.getUserId()));
        break;
      }
      case "3": {
        int id = promptInt("Order ID: ");
        Order o = orderModel.findOrderById(id);
        if (o == null) {
          System.out.println("Not found.");
        } else {
          printOrder(o);
        }
        break;
      }
      case "4": {
        int id = promptInt("Order ID: ");
        boolean ok = orderModel.cancelOrder(id);
        System.out.println(ok ? "Cancelled." : "Failed.");
        break;
      }
      case "5": {
        int id = promptInt("Order ID: ");
        BigDecimal total = orderModel.calculateOrderTotal(id);
        System.out.println("Order #" + id + " total = $" + total);
        break;
      }
      case "6":
        if (isAdmin) {
          printOrders(orderModel.findAllOrders());
        }
        break;
      case "7":
        if (isAdmin) {
          String status = prompt("Status: ");
          printOrders(orderModel.findOrdersByStatus(status));
        }
        break;
      case "8":
        if (isAdmin) {
          int id = promptInt("Order ID: ");
          String status = prompt("New status: ");
          boolean ok = orderModel.updateOrderStatus(id, status);
          System.out.println(ok ? "Status updated." : "Failed.");
        }
        break;
      case "9":
        if (isAdmin) {
          int id = promptInt("Order ID: ");
          boolean ok = orderModel.deleteOrder(id);
          System.out.println(ok ? "Deleted." : "Failed.");
        }
        break;
      default:
    }
  }

  // =====================================================
  // Cart / Order Items
  // =====================================================

  private void cartMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Cart (Order Items) ---");
    System.out.println("1. Add item to order");
    System.out.println("2. View items for an order");
    System.out.println("3. Update item quantity");
    System.out.println("4. Remove item");
    System.out.println("5. Clear all items from an order");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        int orderId = promptInt("Order ID: ");
        int productId = promptInt("Product ID: ");
        int qty = promptInt("Quantity: ");
        Product p = productModel.findProductById(productId);
        if (p == null) {
          System.out.println("Product not found.");
          break;
        }
        OrderItem item = itemModel.addItem(orderId, productId, qty, p.getUnitPrice());
        if (item != null) {
          System.out.println("Added. Subtotal (via trigger) = $" + item.getSubtotal());
        }
        break;
      }
      case "2": {
        int id = promptInt("Order ID: ");
        List<OrderItem> items = itemModel.findItemsByOrder(id);
        if (items.isEmpty()) {
          System.out.println("(empty)");
        }
        for (OrderItem it : items) {
          System.out.println("  product=" + it.getProductId()
              + " qty=" + it.getQuantity()
              + " price=$" + it.getUnitPrice()
              + " subtotal=$" + it.getSubtotal());
        }
        break;
      }
      case "3": {
        int orderId = promptInt("Order ID: ");
        int productId = promptInt("Product ID: ");
        int qty = promptInt("New quantity: ");
        boolean ok = itemModel.updateQuantity(orderId, productId, qty);
        System.out.println(ok ? "Updated (subtotal recomputed by trigger)." : "Failed.");
        break;
      }
      case "4": {
        int orderId = promptInt("Order ID: ");
        int productId = promptInt("Product ID: ");
        boolean ok = itemModel.removeItem(orderId, productId);
        System.out.println(ok ? "Removed." : "Failed.");
        break;
      }
      case "5": {
        int id = promptInt("Order ID: ");
        boolean ok = itemModel.clearOrder(id);
        System.out.println(ok ? "Cleared." : "Failed.");
        break;
      }
      default:
    }
  }

  // =====================================================
  // Payments
  // =====================================================

  private void paymentsMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Payments ---");
    System.out.println("1. Create payment");
    System.out.println("2. Find payment by ID");
    System.out.println("3. Find payment by order");
    System.out.println("4. Find all payments");
    System.out.println("5. Mark as paid");
    System.out.println("6. Update payment status");
    System.out.println("7. Delete payment");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        int orderId = promptInt("Order ID: ");
        String method = prompt("Method (credit/paypal): ");
        Payment p = paymentModel.createPayment(orderId, method);
        if (p != null) {
          printPayment(p);
        }
        break;
      }
      case "2": {
        int id = promptInt("Payment ID: ");
        Payment p = paymentModel.findPaymentById(id);
        if (p == null) {
          System.out.println("Not found.");
        } else {
          printPayment(p);
        }
        break;
      }
      case "3": {
        int id = promptInt("Order ID: ");
        Payment p = paymentModel.findPaymentByOrderId(id);
        if (p == null) {
          System.out.println("No payment for that order.");
        } else {
          printPayment(p);
        }
        break;
      }
      case "4": {
        List<Payment> all = paymentModel.findAllPayments();
        for (Payment p : all) {
          printPayment(p);
        }
        break;
      }
      case "5": {
        int id = promptInt("Payment ID: ");
        boolean ok = paymentModel.markAsPaid(id);
        System.out.println(ok ? "Marked as paid." : "Failed.");
        break;
      }
      case "6": {
        int id = promptInt("Payment ID: ");
        String status = prompt("New status (paid/failed/refunded): ");
        boolean ok = paymentModel.updatePaymentStatus(id, status);
        System.out.println(ok ? "Updated." : "Failed.");
        break;
      }
      case "7": {
        int id = promptInt("Payment ID: ");
        boolean ok = paymentModel.deletePayment(id);
        System.out.println(ok ? "Deleted." : "Failed.");
        break;
      }
      default:
    }
  }

  // =====================================================
  // Shipments
  // =====================================================

  private void shipmentsMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Shipments ---");
    System.out.println("1. Create shipment");
    System.out.println("2. Find by ID");
    System.out.println("3. Find by order");
    System.out.println("4. Find all");
    System.out.println("5. Mark as shipped");
    System.out.println("6. Mark as delivered");
    System.out.println("7. Delete shipment");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        int orderId = promptInt("Order ID: ");
        String tracking = prompt("Tracking number: ");
        String carrier = prompt("Carrier (UPS/FedEx/USPS): ");
        Shipment s = shipmentModel.createShipment(orderId, tracking, carrier);
        if (s != null) {
          printShipment(s);
        }
        break;
      }
      case "2": {
        int id = promptInt("Shipment ID: ");
        Shipment s = shipmentModel.findShipmentById(id);
        if (s == null) {
          System.out.println("Not found.");
        } else {
          printShipment(s);
        }
        break;
      }
      case "3": {
        int id = promptInt("Order ID: ");
        Shipment s = shipmentModel.findShipmentByOrderId(id);
        if (s == null) {
          System.out.println("No shipment for that order.");
        } else {
          printShipment(s);
        }
        break;
      }
      case "4": {
        List<Shipment> all = shipmentModel.findAllShipments();
        for (Shipment s : all) {
          printShipment(s);
        }
        break;
      }
      case "5": {
        int id = promptInt("Shipment ID: ");
        boolean ok = shipmentModel.markAsShipped(id);
        System.out.println(ok ? "Marked as shipped." : "Failed.");
        break;
      }
      case "6": {
        int id = promptInt("Shipment ID: ");
        boolean ok = shipmentModel.markAsDelivered(id);
        System.out.println(ok ? "Marked as delivered." : "Failed.");
        break;
      }
      case "7": {
        int id = promptInt("Shipment ID: ");
        boolean ok = shipmentModel.deleteShipment(id);
        System.out.println(ok ? "Deleted." : "Failed.");
        break;
      }
      default:
    }
  }

  // =====================================================
  // Order Logs
  // =====================================================

  private void orderLogsMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Order Logs ---");
    System.out.println("1. Add log entry");
    System.out.println("2. View logs by order");
    System.out.println("3. View recent logs");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        int orderId = promptInt("Order ID: ");
        String type = prompt("Action type: ");
        String desc = prompt("Description: ");
        OrderLog log = logModel.addLog(orderId, type, desc);
        if (log != null) {
          System.out.println("Log #" + log.getLogId() + " created.");
        }
        break;
      }
      case "2": {
        int id = promptInt("Order ID: ");
        List<OrderLog> logs = logModel.findLogsByOrder(id);
        for (OrderLog log : logs) {
          System.out.println("  [" + log.getCreatedAt() + "] "
              + log.getActionType() + ": " + log.getActionDescription());
        }
        break;
      }
      case "3": {
        int limit = promptInt("How many: ");
        List<OrderLog> logs = logModel.findRecentLogs(limit);
        for (OrderLog log : logs) {
          System.out.println("  order=" + log.getOrderId()
              + " [" + log.getCreatedAt() + "] "
              + log.getActionType() + ": " + log.getActionDescription());
        }
        break;
      }
      default:
    }
  }

  // =====================================================
  // Notifications
  // =====================================================

  private void notificationsMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Email Notifications ---");
    System.out.println("1. Send notification");
    System.out.println("2. Find by order");
    System.out.println("3. Find all");
    System.out.println("4. Update status");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        int orderId = promptInt("Order ID: ");
        String email = prompt("Recipient email: ");
        String subject = prompt("Subject: ");
        EmailNotification n = notifModel.sendNotification(orderId, email, subject);
        if (n != null) {
          System.out.println("Notification #" + n.getNotificationId() + " sent.");
        }
        break;
      }
      case "2": {
        int id = promptInt("Order ID: ");
        List<EmailNotification> list = notifModel.findNotificationsByOrder(id);
        for (EmailNotification n : list) {
          System.out.println("  [" + n.getStatus() + "] to " + n.getEmail() + ": " + n.getSubject());
        }
        break;
      }
      case "3": {
        List<EmailNotification> all = notifModel.findAllNotifications();
        for (EmailNotification n : all) {
          System.out.println("  #" + n.getNotificationId()
              + " order=" + n.getOrderId()
              + " [" + n.getStatus() + "] " + n.getSubject());
        }
        break;
      }
      case "4": {
        int id = promptInt("Notification ID: ");
        String status = prompt("New status: ");
        boolean ok = notifModel.updateStatus(id, status);
        System.out.println(ok ? "Updated." : "Failed.");
        break;
      }
      default:
    }
  }

  // =====================================================
  // Currency
  // =====================================================

  private void currencyMenu() throws SQLException {
    System.out.println();
    System.out.println("--- Currency / Exchange Rates ---");
    System.out.println("1. Add rate");
    System.out.println("2. Find all rates");
    System.out.println("3. Find rate by currency");
    System.out.println("4. Update rate");
    System.out.println("5. Delete rate");
    System.out.println("6. Convert amount (SQL function)");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        String code = prompt("Currency code (USD/EUR/etc.): ");
        BigDecimal rate = promptDecimal("Exchange rate: ");
        ExchangeRate r = rateModel.addRate(code, rate);
        if (r != null) {
          System.out.println("Added: " + r.getCurrency() + " = " + r.getExchangeRate());
        }
        break;
      }
      case "2": {
        List<ExchangeRate> all = rateModel.findAllRates();
        for (ExchangeRate r : all) {
          System.out.println("  " + r.getCurrency() + " = " + r.getExchangeRate());
        }
        break;
      }
      case "3": {
        String code = prompt("Currency: ");
        ExchangeRate r = rateModel.findRateByCurrency(code);
        if (r == null) {
          System.out.println("Not found.");
        } else {
          System.out.println(r.getCurrency() + " = " + r.getExchangeRate());
        }
        break;
      }
      case "4": {
        String code = prompt("Currency: ");
        BigDecimal rate = promptDecimal("New rate: ");
        boolean ok = rateModel.updateRate(code, rate);
        System.out.println(ok ? "Updated." : "Failed.");
        break;
      }
      case "5": {
        int id = promptInt("Rate ID: ");
        boolean ok = rateModel.deleteRate(id);
        System.out.println(ok ? "Deleted." : "Failed.");
        break;
      }
      case "6": {
        BigDecimal amount = promptDecimal("Amount: ");
        String code = prompt("Target currency: ");
        BigDecimal converted = rateModel.convertAmount(amount, code);
        System.out.println("$" + amount + " -> " + converted + " " + code);
        break;
      }
      default:
    }
  }

  // =====================================================
  // User Management (admin)
  // =====================================================

  private void userManagementMenu() throws SQLException {
    System.out.println();
    System.out.println("--- User Management ---");
    System.out.println("1. View all users");
    System.out.println("2. Find user by ID");
    System.out.println("3. Promote to admin");
    System.out.println("4. Demote from admin");
    System.out.println("5. Delete any user");
    System.out.println("6. Count users");
    System.out.println("0. Back");
    switch (prompt("Choose: ")) {
      case "1": {
        List<IBaseUser> all = userModel.viewAllUsers();
        for (IBaseUser u : all) {
          printUser(u);
        }
        break;
      }
      case "2": {
        int id = promptInt("User ID: ");
        IBaseUser u = userModel.findUserById(id);
        if (u == null) {
          System.out.println("Not found.");
        } else {
          printUser(u);
        }
        break;
      }
      case "3": {
        int id = promptInt("User ID: ");
        IBaseUser u = userModel.promoteToAdmin(id);
        if (u != null) {
          System.out.println(u.getName() + " promoted to admin.");
        }
        break;
      }
      case "4": {
        int id = promptInt("User ID: ");
        IBaseUser u = userModel.demoteFromAdmin(id);
        if (u != null) {
          System.out.println(u.getName() + " demoted.");
        }
        break;
      }
      case "5": {
        int id = promptInt("User ID: ");
        String c = prompt("Type 'yes' to confirm: ");
        if ("yes".equalsIgnoreCase(c)) {
          boolean ok = userModel.deleteAnyUser(id);
          System.out.println(ok ? "Deleted." : "Failed.");
        }
        break;
      }
      case "6": {
        int count = userModel.countUsers();
        System.out.println("Total users: " + count);
        break;
      }
      default:
    }
  }

  // =====================================================
  // Helpers
  // =====================================================

  private String prompt(String label) {
    System.out.print(label);
    return in.nextLine().trim();
  }

  private int promptInt(String label) {
    while (true) {
      try {
        return Integer.parseInt(prompt(label));
      } catch (NumberFormatException e) {
        System.out.println("Enter a valid integer.");
      }
    }
  }

  private BigDecimal promptDecimal(String label) {
    while (true) {
      try {
        return new BigDecimal(prompt(label));
      } catch (NumberFormatException e) {
        System.out.println("Enter a valid number.");
      }
    }
  }

  private void printUser(IBaseUser u) {
    System.out.println("  #" + u.getUserId()
        + " " + u.getName()
        + " <" + u.getEmail() + ">"
        + " admin=" + u.isAdmin()
        + " oauth=" + u.getOauthProvider());
  }

  private void printProduct(Product p) {
    System.out.println("  #" + p.getProductId()
        + " " + p.getProductName()
        + " $" + p.getUnitPrice()
        + " [" + p.getSpecification() + "]"
        + " " + p.getProductUrl());
  }

  private void printProducts(List<Product> list) {
    if (list.isEmpty()) {
      System.out.println("(no products)");
    }
    for (Product p : list) {
      printProduct(p);
    }
  }

  private void printOrder(Order o) {
    System.out.println("  #" + o.getOrderId()
        + " user=" + o.getUserId()
        + " date=" + o.getOrderDate()
        + " status=" + o.getStatus()
        + " shipping=" + o.getShippingMethod()
        + " payment=" + o.getPaymentMethod()
        + " total=$" + o.getTotalAmount());
  }

  private void printOrders(List<Order> list) {
    if (list.isEmpty()) {
      System.out.println("(no orders)");
    }
    for (Order o : list) {
      printOrder(o);
    }
  }

  private void printPayment(Payment p) {
    System.out.println("  #" + p.getPaymentId()
        + " order=" + p.getOrderId()
        + " method=" + p.getPaymentMethod()
        + " status=" + p.getPaymentStatus()
        + " paidAt=" + p.getPaidAt());
  }

  private void printShipment(Shipment s) {
    System.out.println("  #" + s.getShipmentId()
        + " order=" + s.getOrderId()
        + " tracking=" + s.getTrackingNumber()
        + " carrier=" + s.getCarrier()
        + " status=" + s.getShipmentStatus()
        + " shipped=" + s.getShippedAt()
        + " delivered=" + s.getDeliveredAt());
  }
}
