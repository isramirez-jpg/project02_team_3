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

public class DatabaseManager {
  private Connection sqliteConnection;

  public DatabaseManager() {
    try {
      // Establish SQLite connection
      sqliteConnection = DriverManager.getConnection("jdbc:sqlite:app.db");

      // Enable Foreign Keys in SQLite
      try (Statement stmt = sqliteConnection.createStatement()) {
        stmt.execute("PRAGMA foreign_keys = ON;");
      }
      // Initialize the database tables if they do not exist
      initTables();
    } catch (SQLException e) {
      // If there is an exception during the connection or table initialization,
      // print the stack trace for debugging
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
    }
  }

  /**
   * The registerUser method is used to register a new user in the database,
   * including their profile and address information
   *
   * @param username  the username for the new user
   * @param password  the plain-text password for the new user (will be hashed before storage)
   * @param email     the email address for the new user
   * @param firstName the first name of the new user
   * @param lastName  the last name of the new user
   * @param phone     the phone number of the new user
   * @param street    the street address of the new user
   * @param city      the city of the new user
   * @param state     the state/province of the new user
   * @param zip       the ZIP/postal code of the new user
   * @param roleName  the role name to assign to the new user (e.g., "USER", "ADMIN")
   * @return true if the user was successfully registered, otherwise false
   */
  public boolean registerUser(String username, String password, String email,
      String firstName, String lastName, String phone, String street,
      String city, String state, String zip, String roleName) {

    String insertUserSql = "INSERT INTO users (username, password_hash, email, role_id) VALUES (?, ?, ?, (SELECT role_id FROM roles WHERE role_name = ?));";
    String insertCustomerSql = "INSERT INTO customers (user_id, first_name, last_name, phone) VALUES (?, ?, ?, ?);";
    String insertAddressSql = "INSERT INTO addresses (customer_id, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?);";

    try {
      // Begin Transaction
      sqliteConnection.setAutoCommit(false);

      // Insert User
      long userId = -1;
      try (PreparedStatement pStatement = sqliteConnection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
        pStatement.setString(1, username);
        // Hash and salt password using BCrypt before saving it to the database
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        // Set the hashed password
        pStatement.setString(2, hashedPassword);
        pStatement.setString(3, email);
        pStatement.setString(4, roleName);
        pStatement.executeUpdate();

        ResultSet rs = pStatement.getGeneratedKeys();
        if (rs.next()) {
          userId = rs.getLong(1);
        }
      }

      if (userId == -1) {
        sqliteConnection.rollback();
        return false;
      }

      // Insert Customer Profile
      long customerId = -1;
      try (PreparedStatement pStatement = sqliteConnection.prepareStatement(insertCustomerSql, Statement.RETURN_GENERATED_KEYS)) {
        pStatement.setLong(1, userId);
        pStatement.setString(2, firstName);
        pStatement.setString(3, lastName);
        pStatement.setString(4, phone);
        pStatement.executeUpdate();

        ResultSet rs = pStatement.getGeneratedKeys();
        if (rs.next()) {
          customerId = rs.getLong(1);
        }
      }

      if (customerId == -1) {
        sqliteConnection.rollback();
        return false;
      }

      // Insert Address
      try (PreparedStatement pStatement = sqliteConnection.prepareStatement(insertAddressSql)) {
        pStatement.setLong(1, customerId);
        pStatement.setString(2, street);
        pStatement.setString(3, city);
        pStatement.setString(4, state);
        pStatement.setString(5, zip);
        pStatement.executeUpdate();
      }

      // Commit Transaction
      sqliteConnection.commit();
      return true;

    } catch (SQLException e) {
      try {
        sqliteConnection.rollback();
      } catch (SQLException rollbackEx) {
        rollbackEx.printStackTrace();
      }
      e.printStackTrace();
      return false;
    } finally {
      try {
        sqliteConnection.setAutoCommit(true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * The authenticateUser method is used to verify user credentials.
   *
   * @param username  the username for the new user
   * @param password  the plain-text password for the new user (will be hashed before storage)
   * @return true if the user was successfully authenticated, otherwise false
   */
  public boolean authenticateUser(String username, String password) {
    String sql = "SELECT password_hash FROM users WHERE username = ? AND is_active = 1";
    try (PreparedStatement pStatement = sqliteConnection.prepareStatement(sql)) {
      pStatement.setString(1, username);
      ResultSet rs = pStatement.executeQuery();
      if (rs.next()) {
        String storedPassword = rs.getString("password_hash");
        // Verify the plain text password against the stored BCrypt hash
        return BCrypt.checkpw(password, storedPassword);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * The getCustomerNameByUsername method is used to get the
   * first and last name for username passed in from the customers table.
   *
   * @param username  the username for the user
   * @return a string displaying the first name concatenated with a space and last name.
   */
  public String getCustomerNameByUsername(String username) {
    String sql = """
        SELECT c.first_name, c.last_name 
        FROM customers c 
        JOIN users u ON c.user_id = u.user_id 
        WHERE u.username = ?
    """;
    try (PreparedStatement pStatement = sqliteConnection.prepareStatement(sql)) {
      pStatement.setString(1, username);
      ResultSet rs = pStatement.executeQuery();
      if (rs.next()) {
        return rs.getString("first_name") + " " + rs.getString("last_name");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Guest";
  }

  /**
   * The getAllItems method is used to get all the items from the items table
   *
   * @return a list of all items in the items table
   */
  public List<String> getAllItems() {
    List<String> items = new ArrayList<>();
    String sql = "SELECT name FROM items ORDER BY id DESC";
    try (Statement stmt = sqliteConnection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        items.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return items;
  }

  /**
   * The insertItem method is used to insert a new item into the items table.
   *
   * @param name the name of the item to insert
   */
  public void insertItem(String name) {
    String sql = "INSERT INTO items(name) VALUES(?)";
    try (PreparedStatement pStatement = sqliteConnection.prepareStatement(sql)) {
      pStatement.setString(1, name);
      pStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * The deleteItem method is used to remove selected item from items table.
   *
   * @param name the name of the item to delete
   */
  public void deleteItem(String name) {
    String sql = "DELETE FROM items WHERE name = ?";
    try (PreparedStatement pStatement = sqliteConnection.prepareStatement(sql)) {
      pStatement.setString(1, name);
      pStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
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