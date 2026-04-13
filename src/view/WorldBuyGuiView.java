package view;

import controller.uiFeatures.UiFeatures;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.order.Order;
import model.user.BaseUserAbstract;
import model.user.IBaseUser;

/**
 * Swing implementation of the WorldBuy GUI view.
 * one JFrame with three cards swapped via CardLayout:
 *   - login / register card (unauthenticated)
 *   - user dashboard card (regular user: profile + orders)
 *   - admin dashboard card (admin: users table + all orders table)
 * implements addFeatures to wire button listeners, and six display callbacks
 * so the controller can push updates back via the features pattern.
 */
public class WorldBuyGuiView extends JFrame implements IguiViewWorldBuy {

  private static final String LOGIN_CARD = "login";
  private static final String USER_CARD = "user";
  private static final String ADMIN_CARD = "admin";

  private UiFeatures features;
  private IBaseUser currentUser;

  private final CardLayout cardLayout;
  private final JPanel cardPanel;

  // Login widgets
  private JTextField loginEmailField;
  private JTextField registerNameField;
  private JTextField registerEmailField;
  private JTextField registerOauthField;
  private JLabel loginStatusLabel;

  // User dashboard widgets
  private JLabel userWelcomeLabel;
  private DefaultTableModel userOrdersTableModel;
  private JTable userOrdersTable;
  private JLabel userStatusLabel;

  // Admin dashboard widgets
  private JLabel adminWelcomeLabel;
  private DefaultTableModel allUsersTableModel;
  private JTable allUsersTable;
  private DefaultTableModel allOrdersTableModel;
  private JTable allOrdersTable;
  private JLabel adminStatusLabel;

  /**
   * constructs the main window, builds all three cards, and shows the login card.
   */
  public WorldBuyGuiView() {
    super("WorldBuy");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1000, 700);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    cardPanel.add(buildLoginPanel(), LOGIN_CARD);
    cardPanel.add(buildUserDashboard(), USER_CARD);
    cardPanel.add(buildAdminDashboard(), ADMIN_CARD);

    setContentPane(cardPanel);
    cardLayout.show(cardPanel, LOGIN_CARD);
  }

  // =====================================================
  // Layout: Login
  // =====================================================

  private JPanel buildLoginPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    JLabel title = new JLabel("WorldBuy", JLabel.CENTER);
    title.setFont(title.getFont().deriveFont(32f));
    panel.add(title, BorderLayout.NORTH);

    JPanel forms = new JPanel(new GridLayout(1, 2, 20, 0));

    // Login form
    JPanel loginForm = new JPanel();
    loginForm.setLayout(new BoxLayout(loginForm, BoxLayout.Y_AXIS));
    loginForm.setBorder(BorderFactory.createTitledBorder("Login"));
    JLabel loginEmailLabel = new JLabel("Email:");
    loginEmailLabel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    loginForm.add(loginEmailLabel);
    loginEmailField = new JTextField(20);
    capHeight(loginEmailField);
    loginForm.add(loginEmailField);
    loginForm.add(Box.createVerticalStrut(10));
    JButton loginBtn = new JButton("Login");
    loginBtn.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    loginForm.add(loginBtn);
    loginForm.add(Box.createVerticalGlue());
    forms.add(loginForm);

    // Register form
    JPanel regForm = new JPanel();
    regForm.setLayout(new BoxLayout(regForm, BoxLayout.Y_AXIS));
    regForm.setBorder(BorderFactory.createTitledBorder("Register"));
    JLabel nameLabel = new JLabel("Name:");
    nameLabel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    regForm.add(nameLabel);
    registerNameField = new JTextField(20);
    capHeight(registerNameField);
    regForm.add(registerNameField);
    JLabel regEmailLabel = new JLabel("Email:");
    regEmailLabel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    regForm.add(regEmailLabel);
    registerEmailField = new JTextField(20);
    capHeight(registerEmailField);
    regForm.add(registerEmailField);
    JLabel oauthLabel = new JLabel("OAuth Provider (google/local):");
    oauthLabel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    regForm.add(oauthLabel);
    registerOauthField = new JTextField("local", 20);
    capHeight(registerOauthField);
    regForm.add(registerOauthField);
    regForm.add(Box.createVerticalStrut(10));
    JButton registerBtn = new JButton("Register");
    registerBtn.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    regForm.add(registerBtn);
    regForm.add(Box.createVerticalGlue());
    forms.add(regForm);

    panel.add(forms, BorderLayout.CENTER);

    loginStatusLabel = new JLabel(" ", JLabel.CENTER);
    panel.add(loginStatusLabel, BorderLayout.SOUTH);

    // Wire buttons in addFeatures (they need the features reference)
    loginBtn.putClientProperty("role", "login");
    registerBtn.putClientProperty("role", "register");

    // Temporarily store for later wiring
    panel.putClientProperty("loginBtn", loginBtn);
    panel.putClientProperty("registerBtn", registerBtn);

    return panel;
  }

  // =====================================================
  // Layout: User Dashboard
  // =====================================================

  private JPanel buildUserDashboard() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    userWelcomeLabel = new JLabel("Welcome", JLabel.LEFT);
    userWelcomeLabel.setFont(userWelcomeLabel.getFont().deriveFont(20f));
    panel.add(userWelcomeLabel, BorderLayout.NORTH);

    // Orders table
    userOrdersTableModel = new DefaultTableModel(
        new Object[]{"Order ID", "Date", "Status", "Shipping", "Payment", "Total"}, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    userOrdersTable = new JTable(userOrdersTableModel);
    userOrdersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    panel.add(new JScrollPane(userOrdersTable), BorderLayout.CENTER);

    // Buttons
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton refreshOrdersBtn = new JButton("Refresh Orders");
    JButton placeOrderBtn = new JButton("Place Order");
    JButton cancelOrderBtn = new JButton("Cancel Selected");
    JButton totalBtn = new JButton("View Total");
    JButton viewProfileBtn = new JButton("View Profile");
    JButton updateProfileBtn = new JButton("Update Profile");
    JButton deleteAccountBtn = new JButton("Delete Account");
    JButton logoutBtn = new JButton("Logout");
    buttons.add(refreshOrdersBtn);
    buttons.add(placeOrderBtn);
    buttons.add(cancelOrderBtn);
    buttons.add(totalBtn);
    buttons.add(viewProfileBtn);
    buttons.add(updateProfileBtn);
    buttons.add(deleteAccountBtn);
    buttons.add(logoutBtn);

    userStatusLabel = new JLabel(" ");
    JPanel south = new JPanel(new BorderLayout());
    south.add(buttons, BorderLayout.NORTH);
    south.add(userStatusLabel, BorderLayout.SOUTH);
    panel.add(south, BorderLayout.SOUTH);

    panel.putClientProperty("refreshOrdersBtn", refreshOrdersBtn);
    panel.putClientProperty("placeOrderBtn", placeOrderBtn);
    panel.putClientProperty("cancelOrderBtn", cancelOrderBtn);
    panel.putClientProperty("totalBtn", totalBtn);
    panel.putClientProperty("viewProfileBtn", viewProfileBtn);
    panel.putClientProperty("updateProfileBtn", updateProfileBtn);
    panel.putClientProperty("deleteAccountBtn", deleteAccountBtn);
    panel.putClientProperty("logoutBtn", logoutBtn);

    return panel;
  }

  // =====================================================
  // Layout: Admin Dashboard
  // =====================================================

  private JPanel buildAdminDashboard() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    adminWelcomeLabel = new JLabel("Welcome, Admin", JLabel.LEFT);
    adminWelcomeLabel.setFont(adminWelcomeLabel.getFont().deriveFont(20f));
    panel.add(adminWelcomeLabel, BorderLayout.NORTH);

    // Center: two tables stacked
    JPanel tables = new JPanel(new GridLayout(2, 1, 0, 10));

    // Users table
    allUsersTableModel = new DefaultTableModel(
        new Object[]{"User ID", "Name", "Email", "Admin", "OAuth"}, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    allUsersTable = new JTable(allUsersTableModel);
    allUsersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane usersScroll = new JScrollPane(allUsersTable);
    usersScroll.setBorder(BorderFactory.createTitledBorder("All Users"));
    tables.add(usersScroll);

    // Orders table
    allOrdersTableModel = new DefaultTableModel(
        new Object[]{"Order ID", "User ID", "Date", "Status", "Shipping", "Payment", "Total"}, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    allOrdersTable = new JTable(allOrdersTableModel);
    allOrdersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane ordersScroll = new JScrollPane(allOrdersTable);
    ordersScroll.setBorder(BorderFactory.createTitledBorder("All Orders"));
    tables.add(ordersScroll);

    panel.add(tables, BorderLayout.CENTER);

    // South: action buttons
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton refreshUsersBtn = new JButton("Refresh Users");
    JButton promoteBtn = new JButton("Promote");
    JButton demoteBtn = new JButton("Demote");
    JButton deleteUserBtn = new JButton("Delete User");
    JButton countBtn = new JButton("Count Users");
    JButton refreshOrdersBtn = new JButton("Refresh Orders");
    JButton updateStatusBtn = new JButton("Update Status");
    JButton deleteOrderBtn = new JButton("Delete Order");
    JButton logoutBtn = new JButton("Logout");
    buttons.add(refreshUsersBtn);
    buttons.add(promoteBtn);
    buttons.add(demoteBtn);
    buttons.add(deleteUserBtn);
    buttons.add(countBtn);
    buttons.add(refreshOrdersBtn);
    buttons.add(updateStatusBtn);
    buttons.add(deleteOrderBtn);
    buttons.add(logoutBtn);

    adminStatusLabel = new JLabel(" ");
    JPanel south = new JPanel(new BorderLayout());
    south.add(buttons, BorderLayout.NORTH);
    south.add(adminStatusLabel, BorderLayout.SOUTH);
    panel.add(south, BorderLayout.SOUTH);

    panel.putClientProperty("refreshUsersBtn", refreshUsersBtn);
    panel.putClientProperty("promoteBtn", promoteBtn);
    panel.putClientProperty("demoteBtn", demoteBtn);
    panel.putClientProperty("deleteUserBtn", deleteUserBtn);
    panel.putClientProperty("countBtn", countBtn);
    panel.putClientProperty("refreshOrdersBtn", refreshOrdersBtn);
    panel.putClientProperty("updateStatusBtn", updateStatusBtn);
    panel.putClientProperty("deleteOrderBtn", deleteOrderBtn);
    panel.putClientProperty("logoutBtn", logoutBtn);

    return panel;
  }

  // =====================================================
  // addFeatures — wires button listeners to features
  // =====================================================

  @Override
  public void addFeatures(UiFeatures features) {
    this.features = features;

    // Login card
    JPanel loginPanel = (JPanel) cardPanel.getComponent(0);
    JButton loginBtn = (JButton) loginPanel.getClientProperty("loginBtn");
    JButton registerBtn = (JButton) loginPanel.getClientProperty("registerBtn");

    loginBtn.addActionListener(e -> features.login(loginEmailField.getText().trim()));

    registerBtn.addActionListener(e -> features.register(
        registerNameField.getText().trim(),
        registerEmailField.getText().trim(),
        registerOauthField.getText().trim()));

    // User dashboard
    JPanel userPanel = (JPanel) cardPanel.getComponent(1);
    ((JButton) userPanel.getClientProperty("refreshOrdersBtn")).addActionListener(e -> {
      if (currentUser != null) {
        features.findOrdersByUser(currentUser.getUserId());
      }
    });
    ((JButton) userPanel.getClientProperty("placeOrderBtn")).addActionListener(e -> {
      if (currentUser == null) {
        return;
      }
      String shipping = JOptionPane.showInputDialog(this, "Shipping method:", "standard");
      if (shipping == null) {
        return;
      }
      String payment = JOptionPane.showInputDialog(this, "Payment method:", "credit");
      if (payment == null) {
        return;
      }
      features.placeOrder(currentUser.getUserId(), shipping, payment);
      features.findOrdersByUser(currentUser.getUserId());
    });
    ((JButton) userPanel.getClientProperty("cancelOrderBtn")).addActionListener(e -> {
      if (currentUser == null) {
        return;
      }
      int orderId = getSelectedOrderId(userOrdersTable, userOrdersTableModel, 0);
      if (orderId <= 0) {
        displayError("Please select an order to cancel");
        return;
      }
      features.cancelOrder(orderId);
      features.findOrdersByUser(currentUser.getUserId());
    });
    ((JButton) userPanel.getClientProperty("totalBtn")).addActionListener(e -> {
      int orderId = getSelectedOrderId(userOrdersTable, userOrdersTableModel, 0);
      if (orderId <= 0) {
        displayError("Please select an order first");
        return;
      }
      features.calculateOrderTotal(orderId);
    });
    ((JButton) userPanel.getClientProperty("viewProfileBtn")).addActionListener(e -> {
      if (currentUser != null) {
        features.viewProfile(currentUser.getUserId());
      }
    });
    ((JButton) userPanel.getClientProperty("updateProfileBtn")).addActionListener(e -> {
      if (currentUser == null) {
        return;
      }
      String newName = JOptionPane.showInputDialog(this, "New name:", currentUser.getName());
      if (newName == null || newName.trim().isEmpty()) {
        return;
      }
      String newEmail = JOptionPane.showInputDialog(this, "New email:", currentUser.getEmail());
      if (newEmail == null || newEmail.trim().isEmpty()) {
        return;
      }
      String newOauth = JOptionPane.showInputDialog(
          this, "OAuth provider:", currentUser.getOauthProvider());
      if (newOauth == null || newOauth.trim().isEmpty()) {
        return;
      }
      IBaseUser updated = BaseUserAbstract.of(
          currentUser.getUserId(),
          newName.trim(),
          newEmail.trim(),
          newOauth.trim(),
          currentUser.isAdmin(),
          currentUser.getCreatedAt());
      features.updateProfile(updated);
    });
    ((JButton) userPanel.getClientProperty("deleteAccountBtn")).addActionListener(e -> {
      if (currentUser == null) {
        return;
      }
      int confirm = JOptionPane.showConfirmDialog(this, "Delete your account?");
      if (confirm == JOptionPane.YES_OPTION) {
        features.deleteAccount(currentUser.getUserId());
        currentUser = null;
        cardLayout.show(cardPanel, LOGIN_CARD);
      }
    });
    ((JButton) userPanel.getClientProperty("logoutBtn")).addActionListener(e -> {
      if (currentUser != null) {
        features.logout(currentUser.getUserId());
      }
      currentUser = null;
      cardLayout.show(cardPanel, LOGIN_CARD);
    });

    // Admin dashboard
    JPanel adminPanel = (JPanel) cardPanel.getComponent(2);
    ((JButton) adminPanel.getClientProperty("refreshUsersBtn"))
        .addActionListener(e -> features.viewAllUsers());
    ((JButton) adminPanel.getClientProperty("promoteBtn")).addActionListener(e -> {
      int userId = getSelectedOrderId(allUsersTable, allUsersTableModel, 0);
      if (userId <= 0) {
        displayError("Please select a user to promote");
        return;
      }
      features.promoteToAdmin(userId);
      features.viewAllUsers();
    });
    ((JButton) adminPanel.getClientProperty("demoteBtn")).addActionListener(e -> {
      int userId = getSelectedOrderId(allUsersTable, allUsersTableModel, 0);
      if (userId <= 0) {
        displayError("Please select a user to demote");
        return;
      }
      features.demoteFromAdmin(userId);
      features.viewAllUsers();
    });
    ((JButton) adminPanel.getClientProperty("deleteUserBtn")).addActionListener(e -> {
      int userId = getSelectedOrderId(allUsersTable, allUsersTableModel, 0);
      if (userId <= 0) {
        displayError("Please select a user to delete");
        return;
      }
      int confirm = JOptionPane.showConfirmDialog(this,
          "Delete user #" + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) {
        return;
      }
      features.deleteAnyUser(userId);
      features.viewAllUsers();
    });
    ((JButton) adminPanel.getClientProperty("countBtn"))
        .addActionListener(e -> features.countUsers());
    ((JButton) adminPanel.getClientProperty("refreshOrdersBtn"))
        .addActionListener(e -> features.findAllOrders());
    ((JButton) adminPanel.getClientProperty("updateStatusBtn")).addActionListener(e -> {
      int orderId = getSelectedOrderId(allOrdersTable, allOrdersTableModel, 0);
      if (orderId <= 0) {
        displayError("Please select an order");
        return;
      }
      String newStatus = JOptionPane.showInputDialog(this, "New status:", "shipped");
      if (newStatus == null || newStatus.trim().isEmpty()) {
        return;
      }
      features.updateOrdersStatus(orderId, newStatus.trim());
      features.findAllOrders();
    });
    ((JButton) adminPanel.getClientProperty("deleteOrderBtn")).addActionListener(e -> {
      int orderId = getSelectedOrderId(allOrdersTable, allOrdersTableModel, 0);
      if (orderId <= 0) {
        displayError("Please select an order to delete");
        return;
      }
      int confirm = JOptionPane.showConfirmDialog(this,
          "Delete order #" + orderId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) {
        return;
      }
      features.deleteOrder(orderId);
      features.findAllOrders();
    });
    ((JButton) adminPanel.getClientProperty("logoutBtn")).addActionListener(e -> {
      if (currentUser != null) {
        features.logout(currentUser.getUserId());
      }
      currentUser = null;
      cardLayout.show(cardPanel, LOGIN_CARD);
    });
  }

  @Override
  public void refreshView() {
    repaint();
  }

  // =====================================================
  // Display callbacks (pushed by the controller)
  // =====================================================

  @Override
  public void displayMessage(String message) {
    setStatus(message, false);
  }

  @Override
  public void displayError(String message) {
    setStatus(message, true);
  }

  @Override
  public void displayUser(IBaseUser user) {
    if (user == null) {
      displayError("No user data to display");
      return;
    }
    if (currentUser == null) {
      // initial login / register — navigate to the right dashboard
      currentUser = user;
      if (user.isAdmin()) {
        adminWelcomeLabel.setText("Welcome, " + user.getName() + " (admin)");
        cardLayout.show(cardPanel, ADMIN_CARD);
        features.viewAllUsers();
        features.findAllOrders();
      } else {
        userWelcomeLabel.setText("Welcome, " + user.getName());
        cardLayout.show(cardPanel, USER_CARD);
        features.findOrdersByUser(user.getUserId());
      }
    } else if (user.getUserId() == currentUser.getUserId()) {
      // updating own profile — refresh state and welcome label
      currentUser = user;
      if (user.isAdmin()) {
        adminWelcomeLabel.setText("Welcome, " + user.getName() + " (admin)");
      } else {
        userWelcomeLabel.setText("Welcome, " + user.getName());
      }
      JOptionPane.showMessageDialog(this,
          "Profile updated:\n"
              + "Name: " + user.getName()
              + "\nEmail: " + user.getEmail()
              + "\nOAuth: " + user.getOauthProvider(),
          "Profile",
          JOptionPane.INFORMATION_MESSAGE);
    } else {
      // viewing someone else's profile — show a read-only dialog
      JOptionPane.showMessageDialog(this,
          "User: " + user.getName()
              + "\nEmail: " + user.getEmail()
              + "\nAdmin: " + user.isAdmin()
              + "\nCreated: " + user.getCreatedAt(),
          "Profile",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  @Override
  public void displayUsers(List<IBaseUser> users) {
    if (users == null) {
      displayError("Could not load users");
      return;
    }
    allUsersTableModel.setRowCount(0);
    for (IBaseUser u : users) {
      if (u == null) {
        continue;
      }
      allUsersTableModel.addRow(new Object[]{
          u.getUserId(), u.getName(), u.getEmail(), u.isAdmin(), u.getOauthProvider()
      });
    }
  }

  @Override
  public void displayOrder(Order order) {
    if (order == null) {
      displayError("No order data to display");
      return;
    }
    JOptionPane.showMessageDialog(this,
        "Order #" + order.getOrderId()
            + "\nUser: " + order.getUserId()
            + "\nDate: " + order.getOrderDate()
            + "\nStatus: " + order.getStatus()
            + "\nShipping: " + order.getShippingMethod()
            + "\nPayment: " + order.getPaymentMethod()
            + "\nTotal: $" + order.getTotalAmount(),
        "Order Details",
        JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void displayOrders(List<Order> orders) {
    if (orders == null) {
      displayError("Could not load orders");
      return;
    }
    if (currentUser != null && currentUser.isAdmin()) {
      allOrdersTableModel.setRowCount(0);
      for (Order o : orders) {
        if (o == null) {
          continue;
        }
        allOrdersTableModel.addRow(new Object[]{
            o.getOrderId(), o.getUserId(), o.getOrderDate(), o.getStatus(),
            o.getShippingMethod(), o.getPaymentMethod(), o.getTotalAmount()
        });
      }
    } else {
      userOrdersTableModel.setRowCount(0);
      for (Order o : orders) {
        if (o == null) {
          continue;
        }
        userOrdersTableModel.addRow(new Object[]{
            o.getOrderId(), o.getOrderDate(), o.getStatus(),
            o.getShippingMethod(), o.getPaymentMethod(), o.getTotalAmount()
        });
      }
    }
  }

  // =====================================================
  // Helpers
  // =====================================================

  private void capHeight(JTextField field) {
    Dimension pref = field.getPreferredSize();
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
    field.setAlignmentX(JPanel.LEFT_ALIGNMENT);
  }

  private int getSelectedOrderId(JTable table, DefaultTableModel model, int idColumn) {
    int row = table.getSelectedRow();
    if (row < 0) {
      return -1;
    }
    Object val = model.getValueAt(row, idColumn);
    return val instanceof Integer ? (Integer) val : -1;
  }

  private void setStatus(String message, boolean isError) {
    JLabel target;
    if (currentUser == null) {
      target = loginStatusLabel;
    } else if (currentUser.isAdmin()) {
      target = adminStatusLabel;
    } else {
      target = userStatusLabel;
    }
    target.setText(message);
    target.setForeground(isError ? Color.RED : Color.BLACK);
  }
}
