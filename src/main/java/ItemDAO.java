import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CRUD operations for items on the management to-do list.
 */
public class ItemDAO {
  private final Connection connection;

  public ItemDAO(Connection connection) {
    this.connection = connection;
  }

  /**
   * The getAllItems method is used to get all the items from the items table
   *
   * @return a list of all items in the items table
   */
  public List<String> getAllItems() {
    List<String> items = new ArrayList<>();
    String sql = "SELECT name FROM items ORDER BY id DESC";
    try (Statement ddlStatement = connection.createStatement();
        ResultSet rs = ddlStatement.executeQuery(sql)) {
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
    try (PreparedStatement pStatement = connection.prepareStatement(sql)) {
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
    try (PreparedStatement pStatement = connection.prepareStatement(sql)) {
      pStatement.setString(1, name);
      pStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}