drop database if exists worldbuy;
create database if not exists worldbuy;
use worldbuy;

-- strong entities

create table users(
    user_id int primary key auto_increment,
    name varchar(64),
    email varchar(64) unique,
    oauth_provider varchar(128),
    is_admin boolean,
    created_at datetime
);

create table products(
    product_id int primary key auto_increment,
    product_url varchar(256),
    product_name varchar(128),
    specification varchar(256),
    unit_price decimal(10,2)
);

create table orders(
    order_id int primary key auto_increment,
    user_id int,
    order_date datetime,
    status varchar(128),
    shipping_method varchar(128),
    payment_method varchar(128),
    total_amount decimal(10,2),

    foreign key (user_id) references users(user_id)
        on delete cascade on update cascade
);

create table payments(
    payment_id int primary key auto_increment,
    order_id int unique,
    payment_method varchar(128),
    payment_status varchar(128),
    paid_at datetime,

    foreign key (order_id) references orders(order_id)
        on delete cascade on update cascade
);

create table shipments(
    shipment_id int primary key auto_increment,
    order_id int unique,
    tracking_number varchar(128),
    carrier varchar(128),
    shipment_status varchar(128),
    shipped_at datetime,
    delivered_at datetime,

    foreign key (order_id) references orders(order_id)
        on delete cascade on update cascade
);

create table order_logs(
    log_id int primary key auto_increment,
    order_id int,
    action_type varchar(128),
    action_description varchar(256),
    created_at datetime,

    foreign key (order_id) references orders(order_id)
        on delete cascade on update cascade
);

create table email_notifications(
    notification_id int primary key auto_increment,
    order_id int,
    email varchar(128),
    subject varchar(256),
    sent_at datetime,
    status varchar(128),

    foreign key (order_id) references orders(order_id)
        on delete cascade on update cascade
);

create table exchange_rates(
    rate_id int primary key auto_increment,
    currency varchar(64),
    exchange_rate decimal(10,4),
    updated_at datetime
);

-------------------------------------------------------

-- M:N table (orders ↔ products)

create table order_items(
    order_id int,
    product_id int,

    quantity int,
    unit_price decimal(10,2),
    subtotal decimal(10,2),

    primary key (order_id, product_id),

    foreign key (order_id) references orders(order_id)
        on delete cascade on update cascade,
    foreign key (product_id) references products(product_id)
        on delete cascade on update cascade
);


-- =====================================================
-- STORED PROCEDURES — USERS
-- =====================================================

-- Registers a new user. Always creates a non-admin account.
-- Returns the full user row (so Java can build a StandardUser).
DROP PROCEDURE IF EXISTS register_user;
DELIMITER //
CREATE PROCEDURE register_user(
    IN p_name VARCHAR(64),
    IN p_email VARCHAR(64),
    IN p_oauth VARCHAR(128)
)
BEGIN
    INSERT INTO users (name, email, oauth_provider, is_admin, created_at)
    VALUES (p_name, p_email, p_oauth, FALSE, NOW());

    SELECT user_id, name, email, oauth_provider, is_admin, created_at
      FROM users
     WHERE user_id = LAST_INSERT_ID();
END //
DELIMITER ;


-- Looks up a user by email. Used for login.
DROP PROCEDURE IF EXISTS find_user_by_email;
DELIMITER //
CREATE PROCEDURE find_user_by_email(IN p_email VARCHAR(64))
BEGIN
    SELECT user_id, name, email, oauth_provider, is_admin, created_at
      FROM users
     WHERE email = p_email;
END //
DELIMITER ;


-- Looks up a user by user_id. Used for viewProfile.
DROP PROCEDURE IF EXISTS find_user_by_id;
DELIMITER //
CREATE PROCEDURE find_user_by_id(IN p_user_id INT)
BEGIN
    SELECT user_id, name, email, oauth_provider, is_admin, created_at
      FROM users
     WHERE user_id = p_user_id;
END //
DELIMITER ;


-- Updates a user's editable fields (name, email, oauth provider).
-- Does not touch is_admin or created_at.
DROP PROCEDURE IF EXISTS update_user_profile;
DELIMITER //
CREATE PROCEDURE update_user_profile(
    IN p_user_id INT,
    IN p_name VARCHAR(64),
    IN p_email VARCHAR(64),
    IN p_oauth VARCHAR(128)
)
BEGIN
    UPDATE users
       SET name = p_name,
           email = p_email,
           oauth_provider = p_oauth
     WHERE user_id = p_user_id;
END //
DELIMITER ;


-- Deletes a user by user_id. Cascades to orders and their children via FKs.
DROP PROCEDURE IF EXISTS delete_user;
DELIMITER //
CREATE PROCEDURE delete_user(IN p_user_id INT)
BEGIN
    DELETE FROM users WHERE user_id = p_user_id;
END //
DELIMITER ;


-- =====================================================
-- STORED PROCEDURES — PRODUCTS
-- =====================================================

-- Adds a new product (either from a user's product request or admin).
-- Returns the full row so Java can build a Product.
DROP PROCEDURE IF EXISTS add_product;
DELIMITER //
CREATE PROCEDURE add_product(
    IN p_url VARCHAR(256),
    IN p_name VARCHAR(128),
    IN p_spec VARCHAR(256),
    IN p_price DECIMAL(10,2)
)
BEGIN
    INSERT INTO products (product_url, product_name, specification, unit_price)
    VALUES (p_url, p_name, p_spec, p_price);

    SELECT product_id, product_url, product_name, specification, unit_price
      FROM products
     WHERE product_id = LAST_INSERT_ID();
END //
DELIMITER ;


-- Looks up a product by product_id.
DROP PROCEDURE IF EXISTS find_product_by_id;
DELIMITER //
CREATE PROCEDURE find_product_by_id(IN p_product_id INT)
BEGIN
    SELECT product_id, product_url, product_name, specification, unit_price
      FROM products
     WHERE product_id = p_product_id;
END //
DELIMITER ;


-- Returns every product in the catalog.
DROP PROCEDURE IF EXISTS find_all_products;
DELIMITER //
CREATE PROCEDURE find_all_products()
BEGIN
    SELECT product_id, product_url, product_name, specification, unit_price
      FROM products
     ORDER BY product_id;
END //
DELIMITER ;


-- Searches products by a case-insensitive keyword in the name.
DROP PROCEDURE IF EXISTS search_products;
DELIMITER //
CREATE PROCEDURE search_products(IN p_keyword VARCHAR(128))
BEGIN
    SELECT product_id, product_url, product_name, specification, unit_price
      FROM products
     WHERE product_name LIKE CONCAT('%', p_keyword, '%')
     ORDER BY product_name;
END //
DELIMITER ;


-- Updates all editable fields of a product.
DROP PROCEDURE IF EXISTS update_product;
DELIMITER //
CREATE PROCEDURE update_product(
    IN p_product_id INT,
    IN p_url VARCHAR(256),
    IN p_name VARCHAR(128),
    IN p_spec VARCHAR(256),
    IN p_price DECIMAL(10,2)
)
BEGIN
    UPDATE products
       SET product_url = p_url,
           product_name = p_name,
           specification = p_spec,
           unit_price = p_price
     WHERE product_id = p_product_id;
END //
DELIMITER ;


-- Admin convenience: updates just the price for a product.
DROP PROCEDURE IF EXISTS update_product_price;
DELIMITER //
CREATE PROCEDURE update_product_price(
    IN p_product_id INT,
    IN p_price DECIMAL(10,2)
)
BEGIN
    UPDATE products
       SET unit_price = p_price
     WHERE product_id = p_product_id;
END //
DELIMITER ;


-- Deletes a product (cascades to order_items via FK).
DROP PROCEDURE IF EXISTS delete_product;
DELIMITER //
CREATE PROCEDURE delete_product(IN p_product_id INT)
BEGIN
    DELETE FROM products WHERE product_id = p_product_id;
END //
DELIMITER ;


-- =====================================================
-- STORED PROCEDURES — ORDERS
-- =====================================================

-- Places a new order (status='pending', total=0, date=NOW). Returns the new row.
DROP PROCEDURE IF EXISTS place_order;
DELIMITER //
CREATE PROCEDURE place_order(
    IN p_user_id INT,
    IN p_shipping VARCHAR(128),
    IN p_payment VARCHAR(128)
)
BEGIN
    INSERT INTO orders (user_id, order_date, status, shipping_method, payment_method, total_amount)
    VALUES (p_user_id, NOW(), 'pending', p_shipping, p_payment, 0.00);

    SELECT order_id, user_id, order_date, status, shipping_method, payment_method, total_amount
      FROM orders
     WHERE order_id = LAST_INSERT_ID();
END //
DELIMITER ;


-- Looks up an order by order_id.
DROP PROCEDURE IF EXISTS find_order_by_id;
DELIMITER //
CREATE PROCEDURE find_order_by_id(IN p_order_id INT)
BEGIN
    SELECT order_id, user_id, order_date, status, shipping_method, payment_method, total_amount
      FROM orders
     WHERE order_id = p_order_id;
END //
DELIMITER ;


-- Returns all orders placed by a given user (ordered newest first).
DROP PROCEDURE IF EXISTS find_orders_by_user;
DELIMITER //
CREATE PROCEDURE find_orders_by_user(IN p_user_id INT)
BEGIN
    SELECT order_id, user_id, order_date, status, shipping_method, payment_method, total_amount
      FROM orders
     WHERE user_id = p_user_id
     ORDER BY order_date DESC;
END //
DELIMITER ;


-- Returns all orders with a given status (for admin filters).
DROP PROCEDURE IF EXISTS find_orders_by_status;
DELIMITER //
CREATE PROCEDURE find_orders_by_status(IN p_status VARCHAR(128))
BEGIN
    SELECT order_id, user_id, order_date, status, shipping_method, payment_method, total_amount
      FROM orders
     WHERE status = p_status
     ORDER BY order_date DESC;
END //
DELIMITER ;


-- Returns every order in the system (admin-only).
DROP PROCEDURE IF EXISTS find_all_orders;
DELIMITER //
CREATE PROCEDURE find_all_orders()
BEGIN
    SELECT order_id, user_id, order_date, status, shipping_method, payment_method, total_amount
      FROM orders
     ORDER BY order_date DESC;
END //
DELIMITER ;


-- Updates an order's status (used by admin workflow transitions).
DROP PROCEDURE IF EXISTS update_order_status;
DELIMITER //
CREATE PROCEDURE update_order_status(IN p_order_id INT, IN p_status VARCHAR(128))
BEGIN
    UPDATE orders SET status = p_status WHERE order_id = p_order_id;
END //
DELIMITER ;


-- Cancels an order (sets status='cancelled').
DROP PROCEDURE IF EXISTS cancel_order;
DELIMITER //
CREATE PROCEDURE cancel_order(IN p_order_id INT)
BEGIN
    UPDATE orders SET status = 'cancelled' WHERE order_id = p_order_id;
END //
DELIMITER ;


-- Deletes an order (cascades to items, payments, shipments, logs, notifications).
DROP PROCEDURE IF EXISTS delete_order;
DELIMITER //
CREATE PROCEDURE delete_order(IN p_order_id INT)
BEGIN
    DELETE FROM orders WHERE order_id = p_order_id;
END //
DELIMITER ;


-- =====================================================
-- STORED PROCEDURES — ORDER ITEMS
-- =====================================================

-- Adds a product to an order. Subtotal is auto-computed by trigger.
-- Returns the full row after insert.
DROP PROCEDURE IF EXISTS add_order_item;
DELIMITER //
CREATE PROCEDURE add_order_item(
    IN p_order_id INT,
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_unit_price DECIMAL(10,2)
)
BEGIN
    INSERT INTO order_items (order_id, product_id, quantity, unit_price)
    VALUES (p_order_id, p_product_id, p_quantity, p_unit_price);

    SELECT order_id, product_id, quantity, unit_price, subtotal
      FROM order_items
     WHERE order_id = p_order_id AND product_id = p_product_id;
END //
DELIMITER ;


-- Looks up a single order item by composite key.
DROP PROCEDURE IF EXISTS find_order_item;
DELIMITER //
CREATE PROCEDURE find_order_item(IN p_order_id INT, IN p_product_id INT)
BEGIN
    SELECT order_id, product_id, quantity, unit_price, subtotal
      FROM order_items
     WHERE order_id = p_order_id AND product_id = p_product_id;
END //
DELIMITER ;


-- Returns every item for a given order (the full cart/order contents).
DROP PROCEDURE IF EXISTS find_items_by_order;
DELIMITER //
CREATE PROCEDURE find_items_by_order(IN p_order_id INT)
BEGIN
    SELECT order_id, product_id, quantity, unit_price, subtotal
      FROM order_items
     WHERE order_id = p_order_id;
END //
DELIMITER ;


-- Updates the quantity for an item. Subtotal is auto-recomputed by trigger.
DROP PROCEDURE IF EXISTS update_item_quantity;
DELIMITER //
CREATE PROCEDURE update_item_quantity(
    IN p_order_id INT,
    IN p_product_id INT,
    IN p_quantity INT
)
BEGIN
    UPDATE order_items
       SET quantity = p_quantity
     WHERE order_id = p_order_id AND product_id = p_product_id;
END //
DELIMITER ;


-- Removes a single item from an order.
DROP PROCEDURE IF EXISTS remove_order_item;
DELIMITER //
CREATE PROCEDURE remove_order_item(IN p_order_id INT, IN p_product_id INT)
BEGIN
    DELETE FROM order_items
     WHERE order_id = p_order_id AND product_id = p_product_id;
END //
DELIMITER ;


-- Removes all items from an order (clears the cart).
DROP PROCEDURE IF EXISTS clear_order_items;
DELIMITER //
CREATE PROCEDURE clear_order_items(IN p_order_id INT)
BEGIN
    DELETE FROM order_items WHERE order_id = p_order_id;
END //
DELIMITER ;


-- =====================================================
-- STORED PROCEDURES — PAYMENTS
-- =====================================================

-- Creates a new payment for an order. Starts in 'pending' state with no paid_at.
-- Returns the full row after insert.
DROP PROCEDURE IF EXISTS create_payment;
DELIMITER //
CREATE PROCEDURE create_payment(IN p_order_id INT, IN p_method VARCHAR(128))
BEGIN
    INSERT INTO payments (order_id, payment_method, payment_status, paid_at)
    VALUES (p_order_id, p_method, 'pending', NULL);

    SELECT payment_id, order_id, payment_method, payment_status, paid_at
      FROM payments
     WHERE payment_id = LAST_INSERT_ID();
END //
DELIMITER ;


-- Looks up a payment by payment_id.
DROP PROCEDURE IF EXISTS find_payment_by_id;
DELIMITER //
CREATE PROCEDURE find_payment_by_id(IN p_payment_id INT)
BEGIN
    SELECT payment_id, order_id, payment_method, payment_status, paid_at
      FROM payments
     WHERE payment_id = p_payment_id;
END //
DELIMITER ;


-- Looks up the payment for a given order (1:1 lookup via unique order_id).
DROP PROCEDURE IF EXISTS find_payment_by_order_id;
DELIMITER //
CREATE PROCEDURE find_payment_by_order_id(IN p_order_id INT)
BEGIN
    SELECT payment_id, order_id, payment_method, payment_status, paid_at
      FROM payments
     WHERE order_id = p_order_id;
END //
DELIMITER ;


-- Returns every payment (admin view).
DROP PROCEDURE IF EXISTS find_all_payments;
DELIMITER //
CREATE PROCEDURE find_all_payments()
BEGIN
    SELECT payment_id, order_id, payment_method, payment_status, paid_at
      FROM payments
     ORDER BY payment_id DESC;
END //
DELIMITER ;


-- Marks a payment as paid, stamps paid_at with the current time.
DROP PROCEDURE IF EXISTS mark_payment_paid;
DELIMITER //
CREATE PROCEDURE mark_payment_paid(IN p_payment_id INT)
BEGIN
    UPDATE payments
       SET payment_status = 'paid',
           paid_at = NOW()
     WHERE payment_id = p_payment_id;
END //
DELIMITER ;


-- Generic status update (for 'failed', 'refunded', etc.).
DROP PROCEDURE IF EXISTS update_payment_status;
DELIMITER //
CREATE PROCEDURE update_payment_status(IN p_payment_id INT, IN p_status VARCHAR(128))
BEGIN
    UPDATE payments SET payment_status = p_status WHERE payment_id = p_payment_id;
END //
DELIMITER ;


-- Deletes a payment.
DROP PROCEDURE IF EXISTS delete_payment;
DELIMITER //
CREATE PROCEDURE delete_payment(IN p_payment_id INT)
BEGIN
    DELETE FROM payments WHERE payment_id = p_payment_id;
END //
DELIMITER ;


-- =====================================================
-- TRIGGERS
-- =====================================================

-- Auto-computes subtotal on INSERT so Java never has to multiply.
DROP TRIGGER IF EXISTS trg_order_items_subtotal_insert;
DELIMITER //
CREATE TRIGGER trg_order_items_subtotal_insert
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //
DELIMITER ;


-- Auto-recomputes subtotal on UPDATE (e.g., quantity changes).
DROP TRIGGER IF EXISTS trg_order_items_subtotal_update;
DELIMITER //
CREATE TRIGGER trg_order_items_subtotal_update
BEFORE UPDATE ON order_items
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //
DELIMITER ;


-- =====================================================
-- STORED PROCEDURES — ADMIN
-- =====================================================

-- Returns all users in the system (for admin management screens).
DROP PROCEDURE IF EXISTS find_all_users;
DELIMITER //
CREATE PROCEDURE find_all_users()
BEGIN
    SELECT user_id, name, email, oauth_provider, is_admin, created_at
      FROM users
     ORDER BY user_id;
END //
DELIMITER ;


-- Promotes a regular user to admin, then returns the updated row.
DROP PROCEDURE IF EXISTS promote_user_to_admin;
DELIMITER //
CREATE PROCEDURE promote_user_to_admin(IN p_user_id INT)
BEGIN
    UPDATE users SET is_admin = TRUE WHERE user_id = p_user_id;

    SELECT user_id, name, email, oauth_provider, is_admin, created_at
      FROM users
     WHERE user_id = p_user_id;
END //
DELIMITER ;


-- Demotes an admin back to a regular user, then returns the updated row.
DROP PROCEDURE IF EXISTS demote_user_from_admin;
DELIMITER //
CREATE PROCEDURE demote_user_from_admin(IN p_user_id INT)
BEGIN
    UPDATE users SET is_admin = FALSE WHERE user_id = p_user_id;

    SELECT user_id, name, email, oauth_provider, is_admin, created_at
      FROM users
     WHERE user_id = p_user_id;
END //
DELIMITER ;


-- Returns the total number of users in the system.
DROP PROCEDURE IF EXISTS count_users;
DELIMITER //
CREATE PROCEDURE count_users()
BEGIN
    SELECT COUNT(*) AS total FROM users;
END //
DELIMITER ;


-- =====================================================
-- FUNCTIONS
-- =====================================================

-- Returns the total cost of an order by summing its order_items subtotals.
-- Used by the Java app whenever we need the authoritative total for an order.
DROP FUNCTION IF EXISTS calc_order_total;
DELIMITER //
CREATE FUNCTION calc_order_total(p_order_id INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE total DECIMAL(10,2);
    SELECT IFNULL(SUM(subtotal), 0)
      INTO total
      FROM order_items
     WHERE order_id = p_order_id;
    RETURN total;
END //
DELIMITER ;


-- Converts an amount to another currency using the exchange_rates table.
-- Returns the original amount if the currency is not found (safe fallback).
DROP FUNCTION IF EXISTS convert_currency;
DELIMITER //
CREATE FUNCTION convert_currency(p_amount DECIMAL(10,2), p_currency VARCHAR(64))
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE rate DECIMAL(10,4);
    SELECT exchange_rate
      INTO rate
      FROM exchange_rates
     WHERE currency = p_currency
     LIMIT 1;
    IF rate IS NULL THEN
        RETURN p_amount;
    END IF;
    RETURN p_amount * rate;
END //
DELIMITER ;


-- Returns how many orders a given user has placed.
-- Used for user profile stats and admin reports.
DROP FUNCTION IF EXISTS get_user_order_count;
DELIMITER //
CREATE FUNCTION get_user_order_count(p_user_id INT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE cnt INT;
    SELECT COUNT(*)
      INTO cnt
      FROM orders
     WHERE user_id = p_user_id;
    RETURN cnt;
END //
DELIMITER ;


-- test
INSERT INTO users(name, email) VALUES ('test', 'test@gmail.com');

SELECT * FROM users;