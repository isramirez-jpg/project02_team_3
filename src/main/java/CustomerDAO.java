import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles database operations for customer records.
 */
public class CustomerDAO {
  private final Connection connection;

  public CustomerDAO(Connection connection) {
    this.connection = connection;
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
    try (PreparedStatement pStatement = connection.prepareStatement(sql)) {
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
}