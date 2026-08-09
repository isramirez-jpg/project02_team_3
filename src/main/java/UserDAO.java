import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Handles database operations related to User authentication and roles.
 */
public class UserDAO {
  private final Connection connection;

  public UserDAO(Connection connection) {
    this.connection = connection;
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
      String firstName, String lastName, String phone,
      String street, String city, String state, String zip, String roleName) {

    String insertUserSql = "INSERT INTO users (username, password_hash, email, role_id) VALUES (?, ?, ?, (SELECT role_id FROM roles WHERE role_name = ?));";
    String insertCustomerSql = "INSERT INTO customers (user_id, first_name, last_name, phone) VALUES (?, ?, ?, ?);";
    String insertAddressSql = "INSERT INTO addresses (customer_id, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?);";

    try {
      connection.setAutoCommit(false);

      long userId = -1;
      try (PreparedStatement pStatement = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
        pStatement.setString(1, username);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
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
        connection.rollback();
        return false;
      }

      long customerId = -1;
      try (PreparedStatement pStatement = connection.prepareStatement(insertCustomerSql, Statement.RETURN_GENERATED_KEYS)) {
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
        connection.rollback();
        return false;
      }

      try (PreparedStatement pStatement = connection.prepareStatement(insertAddressSql)) {
        pStatement.setLong(1, customerId);
        pStatement.setString(2, street);
        pStatement.setString(3, city);
        pStatement.setString(4, state);
        pStatement.setString(5, zip);
        pStatement.executeUpdate();
      }

      connection.commit();
      return true;

    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        rollbackEx.printStackTrace();
      }
      e.printStackTrace();
      return false;
    } finally {
      try {
        connection.setAutoCommit(true);
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
    try (PreparedStatement pStatement = connection.prepareStatement(sql)) {
      pStatement.setString(1, username);
      ResultSet rs = pStatement.executeQuery();
      if (rs.next()) {
        String storedPassword = rs.getString("password_hash");
        return BCrypt.checkpw(password, storedPassword);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Gets the role for the username passed in from the users table.
   *
   * @param username the users username
   * @return a string containing the role for the username that was passed in
   */
  public String getUserRole(String username) {
    String sqlQuery = "SELECT r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.username = ?";
    try (PreparedStatement pStatement = connection.prepareStatement(sqlQuery)) {
      pStatement.setString(1, username);
      ResultSet resultSet = pStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getString("role_name");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "USER";
  }

  /**
   * Gets all registered users along with their assigned roles from the database.
   *
   * @return a list of UserInfo objects that contains user_id, username, email,
   *         role_name, record creation date and is ordered by user_id.
   */
  public List<UserInfo> getAllUsersForAdmin() {
    List<UserInfo> usersList = new ArrayList<>();
    String sqlQuery = """
            SELECT u.user_id, u.username, u.email, r.role_name, u.created_at
            FROM users u
            JOIN roles r ON u.role_id = r.role_id
            ORDER BY u.user_id ASC
        """;
    try (Statement ddlStatement = connection.createStatement();
        ResultSet resultSet = ddlStatement.executeQuery(sqlQuery)) {
      while (resultSet.next()) {
        usersList.add(new UserInfo(
            resultSet.getInt("user_id"),
            resultSet.getString("username"),
            resultSet.getString("email"),
            resultSet.getString("role_name"),
            resultSet.getString("created_at")
        ));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return usersList;
  }
}