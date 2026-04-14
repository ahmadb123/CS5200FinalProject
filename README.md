# WorldBuy — CS 5200 Final Project

**Group members:** Ahmad Bishara, ShunPo Chang
**Course:** CS 5200 — Database Management Systems
**Semester:** Spring 2026

WorldBuy is an international e-commerce order management system. Customers can register, browse a product catalog, place orders, and track payments and shipments. Administrators can manage users, products, and the full order lifecycle. The application is written in Java and talks to a MySQL database through JDBC, with business logic implemented server-side as stored procedures, triggers, and functions.

---

## 1. README — Building and Running the Project

### 1.1 Prerequisites

Install the following software before building the project. Versions listed are the ones we developed and tested against.

| Software | Version | Download |
|---|---|---|
| Java Development Kit (JDK) | 17 or later | https://www.oracle.com/java/technologies/downloads/ |
| MySQL Community Server | 8.0 or later | https://dev.mysql.com/downloads/mysql/ |
| MySQL Workbench | 8.0 or later | https://dev.mysql.com/downloads/workbench/ |
| MySQL Connector/J (JDBC driver) | 9.6.0 | https://dev.mysql.com/downloads/connector/j/ |
| IntelliJ IDEA (recommended IDE) | 2024.1 or later | https://www.jetbrains.com/idea/download/ |

### 1.2 Expected installation directories

The project does not require any fixed install path — it can live anywhere on disk. The paths below are what we used during development.

| Component | Expected location (macOS) | Expected location (Windows) |
|---|---|---|
| JDK | `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home` | `C:\Program Files\Java\jdk-17` |
| MySQL Server | `/usr/local/mysql` | `C:\Program Files\MySQL\MySQL Server 8.0` |
| MySQL Connector/J | `~/Downloads/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar` | `C:\Users\<you>\Downloads\mysql-connector-j-9.6.0\mysql-connector-j-9.6.0.jar` |
| Project source | `~/Desktop/CS5200FinalProject` | `C:\Users\<you>\Desktop\CS5200FinalProject` |

### 1.3 Step-by-step setup

#### Step 1 — Create the database

1. Start your local MySQL server (port `3306`).
2. Open MySQL Workbench and connect to your local instance as `root`.
3. From the menu, choose **File → Open SQL Script…** and select
   `CS5200FinalProject/CS5200_final_project_tables.sql`.
4. Click the **Execute** (lightning bolt) button to run the entire script.
   This will:
   - Drop and recreate the `worldbuy` database
   - Create all 9 tables with primary keys, foreign keys, and constraints
   - Create all stored procedures, triggers, and functions
   - Insert the sample data used for the demo

Verify the database exists by running:
```sql
USE worldbuy;
SHOW TABLES;
```
You should see 9 tables: `users`, `products`, `orders`, `order_items`, `payments`, `shipments`, `order_logs`, `email_notifications`, and `exchange_rates`.

#### Step 2 — Configure the database connection

Open `src/model/DBConnection.java` and confirm the following constants match your local MySQL setup:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/worldbuy";
private static final String USER     = "root";
private static final String PASSWORD = "";
```

If your MySQL `root` account has a password, update `PASSWORD` accordingly. If MySQL runs on a non-default port, update the URL.

#### Step 3 — Open the project in IntelliJ IDEA

1. Launch IntelliJ IDEA.
2. Choose **File → Open…** and select the `CS5200FinalProject` directory.
3. IntelliJ will detect the `.iml` module file and import the project.
4. When prompted, set the **Project SDK** to a JDK 17+ installation.

#### Step 4 — Add the MySQL JDBC driver as a library

The project's module file references a library named `mysql-connector-j-9.6.0`. You must attach the actual JAR once:

1. In IntelliJ, open **File → Project Structure → Libraries**.
2. Click **+ → Java**.
3. Navigate to and select `mysql-connector-j-9.6.0.jar`.
4. Click **OK** and assign it to the `CS5200FinalProject` module.
5. Click **Apply** then **OK**.

#### Step 5 — Build and run

1. In the Project panel, open `src/Main.java`.
2. Click the green **▶ Run** arrow next to the `main` method, or press **⌃R** (Mac) / **Shift+F10** (Windows).
3. On first run, IntelliJ will compile the project and launch the Swing GUI.

### 1.4 Running from the command line (optional)

If you prefer to run without IntelliJ:

```bash
cd CS5200FinalProject
javac -cp "path/to/mysql-connector-j-9.6.0.jar" -d out/production/CS5200FinalProject $(find src -name "*.java")
java  -cp "out/production/CS5200FinalProject:path/to/mysql-connector-j-9.6.0.jar" Main
```

On Windows, replace `:` with `;` in the classpath separator.

### 1.5 Launch modes

WorldBuy supports two front ends, selected via a command-line flag:

| Command | Mode | Description |
|---|---|---|
| `java Main` | GUI (default) | Launches the Swing graphical interface |
| `java Main --mode gui` | GUI | Same as above, explicit |
| `java Main --mode cli` | CLI | Launches the interactive text console |

### 1.6 Demo credentials

The sample data includes one admin and three standard users:

| Role | Name | Email | OAuth Provider |
|---|---|---|---|
| Admin | Ahmad Bishara | `ahmad@neu.edu` | google |
| Standard | ShunPo Chang | `shunpo@neu.edu` | google |
| Standard | Sarah Johnson | `sarah@example.com` | local |
| Standard | Mike Chen | `mike@example.com` | local |

Log in with any of these emails. Admin users see additional management menus; standard users see only customer-facing functionality.

### 1.7 Troubleshooting

| Problem | Fix |
|---|---|
| `Communications link failure` on startup | Ensure MySQL server is running on `localhost:3306`. |
| `Access denied for user 'root'` | Update the `PASSWORD` constant in `DBConnection.java`. |
| `Unknown database 'worldbuy'` | Re-run `CS5200_final_project_tables.sql` in Workbench. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Attach `mysql-connector-j-9.6.0.jar` as a library (Step 4 above). |
| `No suitable driver found for jdbc:mysql://…` | Same fix as above — JDBC driver JAR not on the classpath. |

---

## 2. Technical Specifications

### 2.1 Host language and runtime

- **Language:** Java 17 (LTS)
- **Runtime:** OpenJDK or Oracle JDK, version 17 or later
- **Build system:** IntelliJ IDEA module (`.iml`); the project can also be compiled with a plain `javac` command as shown in section 1.4.

### 2.2 Database

- **DBMS:** MySQL Community Server 8.0+
- **Schema name:** `worldbuy`
- **Character set:** default MySQL UTF-8 (`utf8mb4`)
- **Default host/port:** `localhost:3306`
- **Connection protocol:** JDBC over TCP

### 2.3 Libraries and frameworks

| Library | Version | Purpose |
|---|---|---|
| **MySQL Connector/J** | 9.6.0 | Official JDBC driver used by every model class to connect to MySQL. Attached to the IntelliJ module as a project library. |
| **Java Standard Library — `java.sql`** | bundled with JDK 17 | `Connection`, `PreparedStatement`, `CallableStatement`, and `ResultSet` are used to execute stored procedures and read results. |
| **Java Standard Library — `javax.swing`** | bundled with JDK 17 | Swing is the GUI toolkit used to build the `WorldBuyGuiView`. No third-party GUI framework is used. |
| **Java Standard Library — `java.util.Scanner`** | bundled with JDK 17 | Powers the interactive text CLI in `ConsoleView`. |

No Maven, Gradle, or external dependency manager is used — the only non-JDK dependency is the MySQL JDBC JAR, attached manually.

### 2.4 Architecture

WorldBuy follows the **Model-View-Controller (MVC)** pattern with an additional **Command pattern** in the controller layer.

- **Model (`src/model/`)** — one package per entity (`user`, `product`, `order`, `order/orderItem`, `payment`, `shipment`, `orderLog`, `emailNotification`, `exchangeRate`). Each package contains:
  - A plain Java object (POJO) representing a table row
  - An interface declaring the available operations
  - A JDBC model class that implements the interface by calling stored procedures via `CallableStatement`
  - All models receive a shared `DBConnection` via constructor injection and extend `AbstractJdbcModel` for common JDBC boilerplate.

- **Controller (`src/controller/`)** — every user action is a separate Command class implementing `Icommand` (e.g., `RegisterCommand`, `PlaceOrderCommand`, `UpdateOrderStatusCommand`, `PromoteToAdminCommand`, `CancelOrderCommand`). This makes operations easy to register, route, and unit-test. UI-facing orchestration lives under `controller/uiFeatures/` (`AuthFeatures`, `OrderFeatures`, `ProfileFeatures`, `AdminFeatures`, `GuiController`).

- **View (`src/view/`)** — two interchangeable front ends:
  - `WorldBuyGuiView` — Swing-based graphical interface (default)
  - `ConsoleView` — text-based CLI for testing and accessibility
  Both implement `IWorldBuyView`, so the controller layer is view-agnostic.

- **Entry point (`src/Main.java`)** — parses the `--mode` flag and instantiates either `GuiLauncher` or `CliLauncher`.

### 2.5 Database programming objects

All business logic that touches the database lives server-side:

- **~30 stored procedures** covering CRUD for users, products, orders, order items, payments, and admin operations. The Java layer never builds raw SQL strings — it only invokes named procedures.
- **2 triggers** on `order_items` that automatically compute `subtotal = quantity * unit_price` on both `INSERT` and `UPDATE`, keeping totals consistent without Java arithmetic.
- **3 user-defined functions:**
  - `calc_order_total(order_id)` — sums an order's item subtotals
  - `convert_currency(amount, currency)` — converts an amount using the `exchange_rates` table
  - `get_user_order_count(user_id)` — returns how many orders a user has placed
- **Referential integrity** — every foreign key uses `ON DELETE CASCADE` and `ON UPDATE CASCADE`, so deleting a user or order automatically cleans up dependent rows (items, payments, shipments, logs, notifications).

### 2.6 Development tools

| Tool | Purpose |
|---|---|
| IntelliJ IDEA | Primary IDE |
| MySQL Workbench | Schema design, EER diagram, query testing, demo client |
| Git / GitHub | Version control |
| macOS Terminal / zsh | Command-line builds and MySQL CLI access |

### 2.7 Project layout

```
CS5200FinalProject/
├── CS5200_final_project_tables.sql   # full DB dump: DDL + DML + procedures + triggers + functions
├── CS5200FinalProject.iml            # IntelliJ module descriptor
├── README.md                         # this file
├── out/                              # IntelliJ compiled output (generated)
└── src/
    ├── Main.java                     # entry point
    ├── controller/                   # commands + uiFeatures
    ├── model/                        # entity packages + DBConnection + AbstractJdbcModel
    └── view/                         # ConsoleView (CLI) + WorldBuyGuiView (Swing)
```
