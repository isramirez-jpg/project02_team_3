import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Name: Ha Nguyen
 * Date: 8/4/2026
 * Explanation: This class provides data access operations for the Category entity.
 * It uses the database connection to perform CRUD (Create, Read, * Update, Delete)
 * operations on the categories table in the SQLite database.
 */
public class CategoryDAO {
    private final DatabaseManager databaseManager;

    /**
     * Creates a CategoryDAO using the provided database manager.
     */
    public CategoryDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new category into the categories table.
     */
    public Category insert(Category category) {
        String sql = """
                INSERT INTO categories (category_name, description)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                return null;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setCategoryId(keys.getInt(1));
                    return category;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves a category by its ID.
     */
    public Category getById(int categoryId) {
        String sql = """
                SELECT category_id, category_name, description
                FROM categories
                WHERE category_id = ?
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Category(
                            resultSet.getInt("category_id"),
                            resultSet.getString("category_name"),
                            resultSet.getString("description")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all categories from the categories table.
     */
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();

        String sql = """
                SELECT category_id, category_name, description
                FROM categories
                ORDER BY category_id
                """;

        try (Statement statement = databaseManager.getConnection()
                .createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Category category = new Category(
                        resultSet.getInt("category_id"),
                        resultSet.getString("category_name"),
                        resultSet.getString("description")
                );

                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Updates an existing category in the categories table.
     *
     * @param category the category containing the updated information
     * @return true if the category was successfully updated,
     *         otherwise false
     */
    public boolean update(Category category) {
        String sql = """
                UPDATE categories
                SET category_name = ?, description = ?
                WHERE category_id = ?
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, category.getCategoryId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks whether any products are currently assigned to a category.
     *
     */

    public boolean hasProducts(int categoryId) {
        String sql = """
            SELECT 1
            FROM products
            WHERE category_id = ?
            LIMIT 1
            """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a category from the categories table by its ID.
     */
    public boolean delete(int categoryId) {
        String sql = """
                DELETE FROM categories
                WHERE category_id = ?
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
