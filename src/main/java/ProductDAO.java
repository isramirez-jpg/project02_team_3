import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Name: Ha Nguyen
 * Date: 8/4/2026
 * Explanation: This class provides data access operations for the Product
 * entity. It uses the database connection to perform CRUD (Create, Read,
 * Update, Delete) operations on the products table in the SQLite database.
 */
public class ProductDAO {
    private final DatabaseManager databaseManager;

    /**
     * Creates a ProductDAO using the provided database manager.
     */
    public ProductDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new product into the products table.
     * The database automatically generates the product ID.
     */
    public Product insert(Product product) {
        String sql = """
                INSERT INTO products (
                    category_id,
                    product_name,
                    description,
                    price,
                    gender,
                    color,
                    size,
                    stock_quantity,
                    image_path
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, product.getCategoryId());
            statement.setString(2, product.getProductName());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setString(5, product.getGender());
            statement.setString(6, product.getColor());
            statement.setString(7, product.getSize());
            statement.setInt(8, product.getStockQuantity());
            statement.setString(9, product.getImagePath());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                return null;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setProductId(keys.getInt(1));
                    return product;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves a product by its ID.
     */
    public Product getById(int productId) {
        String sql = """
                SELECT product_id,
                       category_id,
                       product_name,
                       description,
                       price,
                       gender,
                       color,
                       size,
                       stock_quantity,
                       image_path
                FROM products
                WHERE product_id = ?
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setInt(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getInt("product_id"),
                            resultSet.getInt("category_id"),
                            resultSet.getString("product_name"),
                            resultSet.getString("description"),
                            resultSet.getDouble("price"),
                            resultSet.getString("gender"),
                            resultSet.getString("color"),
                            resultSet.getString("size"),
                            resultSet.getInt("stock_quantity"),
                            resultSet.getString("image_path")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all products from the products table.
     */
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT product_id,
                       category_id,
                       product_name,
                       description,
                       price,
                       gender,
                       color,
                       size,
                       stock_quantity,
                       image_path
                FROM products
                ORDER BY product_id
                """;

        try (Statement statement = databaseManager.getConnection()
                .createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getString("gender"),
                        resultSet.getString("color"),
                        resultSet.getString("size"),
                        resultSet.getInt("stock_quantity"),
                        resultSet.getString("image_path")
                );

                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Updates an existing product in the products table.
     *
     * @param product the product containing the updated information
     * @return true if the product was successfully updated,
     *         otherwise false
     */
    public boolean update(Product product) {
        String sql = """
                UPDATE products
                SET category_id = ?,
                    product_name = ?,
                    description = ?,
                    price = ?,
                    gender = ?,
                    color = ?,
                    size = ?,
                    stock_quantity = ?,
                    image_path = ?
                WHERE product_id = ?
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setInt(1, product.getCategoryId());
            statement.setString(2, product.getProductName());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setString(5, product.getGender());
            statement.setString(6, product.getColor());
            statement.setString(7, product.getSize());
            statement.setInt(8, product.getStockQuantity());
            statement.setString(9, product.getImagePath());
            statement.setInt(10, product.getProductId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a product from the products table by its ID.
     */
    public boolean delete(int productId) {
        String sql = """
                DELETE FROM products
                WHERE product_id = ?
                """;

        try (PreparedStatement statement = databaseManager.getConnection()
                .prepareStatement(sql)) {

            statement.setInt(1, productId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
