import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// Added import for BCrypt password hashing library.
// The reason I chose it over other methods to hash a password
// is because it is a strong and hashing algorithm
// that is widely used for securely storing passwords.
// It automatically handles salting and it is more resistant
// to brute-force attacks.
import org.mindrot.jbcrypt.BCrypt;

/**
 * Manages SQLite database connectivity, foreign key configuration,
 * and table initialization for the application.
 *
 *  @author Ha Nguyen
 *  @author Isabel Ramirez
 *  @author Miguel Quezada
 *  @version 0.1.0
 *  @since 2026-08-02
 *
 */
public class DatabaseManager {
  private Connection sqliteConnection;

  // 08/08/2026 – MQ – DAO Refactor – DAO instance variables
  private UserDAO userDAO;
  private CustomerDAO customerDAO;
  private ItemDAO itemDAO;

  public DatabaseManager() {
    try {
      // Establish SQLite connection
      sqliteConnection = DriverManager.getConnection("jdbc:sqlite:app.db");

      // Enable Foreign Keys in SQLite
      try (Statement ddlStatement = sqliteConnection.createStatement()) {
        ddlStatement.execute("PRAGMA foreign_keys = ON;");
      }
      // Initialize the database tables if they do not exist
      initTables();

      // 08/08/2026 – MQ – DAO Refactor – Instantiate DAOs
      this.userDAO = new UserDAO(sqliteConnection);
      this.customerDAO = new CustomerDAO(sqliteConnection);
      this.itemDAO = new ItemDAO(sqliteConnection);

    } catch (SQLException e) {
      // If there is an exception during the connection or table initialization,
      // print the stack trace for debugging
      e.printStackTrace();
    }
  }

  // Create Constructor Method with parameter for unit tests
  public DatabaseManager(Connection sqlconn) {
    this.sqliteConnection = sqlconn;
    try {
      try (Statement createStatement = sqliteConnection.createStatement()) {
        createStatement.execute("PRAGMA foreign_keys = ON;");
      }
      initTables();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * The initTables method is used to create the necessary
   * tables in the database if they do not exist.
   */
  private void initTables() throws SQLException {
    try (Statement ddlStatement = sqliteConnection.createStatement()) {
      // Create the Roles table
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS roles (
              role_id INTEGER PRIMARY KEY AUTOINCREMENT,
              role_name TEXT NOT NULL UNIQUE CHECK (role_name IN ('USER', 'ADMIN', 'DEVELOPER'))
          );
      """);

      // Populate roles
      ddlStatement.execute("INSERT OR IGNORE INTO roles (role_id, role_name) VALUES (1, 'USER'), (2, 'ADMIN'), (3, 'DEVELOPER');");

      // Create the Users table
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS users (
              user_id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT NOT NULL UNIQUE,
              password_hash TEXT NOT NULL,
              email TEXT NOT NULL UNIQUE,
              role_id INTEGER NOT NULL DEFAULT 1,
              is_active INTEGER NOT NULL DEFAULT 1,
              created_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
              FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
          );
      """);

      // Create the Carts table
      ddlStatement.execute("""
    CREATE TABLE IF NOT EXISTS carts (
        cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER NOT NULL,
        status TEXT NOT NULL DEFAULT 'ACTIVE'
            CHECK (status IN ('ACTIVE', 'COMPLETED')),
        created_at TEXT NOT NULL
            DEFAULT (DATETIME('now', 'localtime')),
        updated_at TEXT NOT NULL
            DEFAULT (DATETIME('now', 'localtime')),
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
    );
""");
      // Create the Cart Items table
      ddlStatement.execute("""
    CREATE TABLE IF NOT EXISTS cart_items (
        cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
        cart_id INTEGER NOT NULL,
        product_id INTEGER NOT NULL,
        quantity INTEGER NOT NULL
            CHECK (quantity >= 1),
        unit_price DECIMAL(10, 2) NOT NULL
            CHECK (unit_price >= 0),
        added_at TEXT NOT NULL
            DEFAULT (DATETIME('now', 'localtime')),
        UNIQUE (cart_id, product_id),
        FOREIGN KEY (cart_id)
            REFERENCES carts(cart_id)
            ON DELETE CASCADE,
        FOREIGN KEY (product_id)
            REFERENCES products(product_id)
            ON DELETE RESTRICT
    );
""");
      // Allow each user to have only one active cart
      ddlStatement.execute("""
    CREATE UNIQUE INDEX IF NOT EXISTS
        one_active_cart_per_user
    ON carts(user_id)
    WHERE status = 'ACTIVE';
""");

      // Create the Customers table
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS customers (
              customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
              user_id INTEGER NOT NULL UNIQUE,
              first_name TEXT NOT NULL,
              last_name TEXT NOT NULL,
              phone TEXT,
              created_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
          );
      """);

      //  Create The Addresses table
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS addresses (
              address_id INTEGER PRIMARY KEY AUTOINCREMENT,
              customer_id INTEGER NOT NULL,
              street TEXT NOT NULL,
              city TEXT NOT NULL,
              state TEXT NOT NULL,
              zip_code TEXT NOT NULL,
              is_default INTEGER NOT NULL DEFAULT 1,
              FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
          );
      """);

      // Create items table for Management To do List
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS items (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              done INTEGER DEFAULT 0,
              created TEXT DEFAULT (DATETIME('now', 'localtime'))
          );
      """);

      // Create categories table
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS categories (
              category_id INTEGER PRIMARY KEY AUTOINCREMENT,
              category_name TEXT NOT NULL,
              description TEXT
          );
      """);

      // Create products table
      ddlStatement.execute("""
          CREATE TABLE IF NOT EXISTS products (
              product_id INTEGER PRIMARY KEY AUTOINCREMENT,
              category_id INTEGER NOT NULL,
              product_name TEXT NOT NULL,
              description TEXT,
              price REAL NOT NULL,
              gender TEXT,
              color TEXT,
              size TEXT NOT NULL,
              stock_quantity INTEGER NOT NULL,
              image_path TEXT,
              FOREIGN KEY (category_id) REFERENCES categories(category_id)
          );
      """);
      // Create Orders table
      ddlStatement.execute("""
        CREATE TABLE IF NOT EXISTS orders (
         order_id INTEGER PRIMARY KEY AUTOINCREMENT,
         user_id INTEGER NOT NULL,
        total_amount DECIMAL(10,2) NOT NULL
            CHECK (total_amount >= 0),
        status TEXT NOT NULL DEFAULT 'PENDING'
            CHECK (status IN ('PENDING','COMPLETED','CANCELLED')),
        created_at TEXT NOT NULL
            DEFAULT (DATETIME('now','localtime')),
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE RESTRICT
       );
    """);
      // Create Order Items table
      ddlStatement.execute("""
    CREATE TABLE IF NOT EXISTS order_items (
        order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
        order_id INTEGER NOT NULL,
        product_id INTEGER NOT NULL,
        quantity INTEGER NOT NULL
            CHECK (quantity >= 1),
        unit_price DECIMAL(10,2) NOT NULL
            CHECK (unit_price >= 0),
        FOREIGN KEY (order_id)
            REFERENCES orders(order_id)
            ON DELETE CASCADE,
        FOREIGN KEY (product_id)
            REFERENCES products(product_id)
            ON DELETE RESTRICT
    );
""");

    }
  }

  // 08/08/2026 – MQ – DAO Refactor – Getter methods for DAOs
  public UserDAO getUserDAO() {
    return userDAO;
  }

  public CustomerDAO getCustomerDAO() {
    return customerDAO;
  }

  public ItemDAO getItemDAO() {
    return itemDAO;
  }

  /**
   * Retrieves the user ID associated with the specified username.
   *
   * @param username the username to search for
   * @return the user's database ID if found, otherwise -1
   */
  public int getUserIdByUsername(String username) {
    String sql = """
            SELECT user_id
            FROM users
            WHERE username = ?
            """;

    try (PreparedStatement statement =
        sqliteConnection.prepareStatement(sql)) {

      statement.setString(1, username);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt("user_id");
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return -1;
  }

  /**
   * Returns the SQLite connection for DAO classes.
   */
  public Connection getConnection() {
    return sqliteConnection;
  }

  /**
   * The close method is used to close the database connection.
   */
  public void close() {
    try {
      if (sqliteConnection != null && !sqliteConnection.isClosed()) {
        sqliteConnection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}