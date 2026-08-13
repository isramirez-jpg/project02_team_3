import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Isabel Ramirez
 * @created 8.13.26
 */
public class CartDaoTest {

    private Connection connection;
    private DatabaseManager db;
    private CartDao cartDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection =
                CartCheckoutTestDatabase.createConnection();

        db = new DatabaseManager(connection, false);
        cartDao = new CartDao(db);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createCartCreatesActiveCart() throws SQLException {

        int userId = insertTestUser();

        Cart cart = cartDao.create(userId);

        assertTrue(cart.getCartId() > 0);
        assertEquals(userId, cart.getUserId());
        assertEquals("ACTIVE", cart.getStatus());
    }
    @Test
    void addProductAddsItemToCart() throws SQLException {

        int userId = insertTestUser();
        int productId = insertTestProduct();

        Cart cart = cartDao.create(userId);

        CartItem item = cartDao.addProduct(
                cart.getCartId(),
                productId,
                2
        );

        assertTrue(item.getCartItemId() > 0);
        assertEquals(cart.getCartId(), item.getCartId());
        assertEquals(productId, item.getProductId());
        assertEquals("Test Shirt", item.getProductName());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void addingSameProductTwiceIncreasesQuantity()
            throws SQLException {

        int userId = insertTestUser();
        int productId = insertTestProduct();

        Cart cart = cartDao.create(userId);

        cartDao.addProduct(
                cart.getCartId(),
                productId,
                1
        );

        CartItem updatedItem =
                cartDao.addProduct(
                        cart.getCartId(),
                        productId,
                        2
                );

        assertEquals(3, updatedItem.getQuantity());

        List<CartItem> items =
                cartDao.findItemsByCartId(
                        cart.getCartId()
                );

        assertEquals(1, items.size());
        assertEquals(3, items.get(0).getQuantity());
    }

    private int insertTestUser() throws SQLException {

        String sql = """
                INSERT INTO users
                    (username, password_hash, email)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, "cart_test_user");
            statement.setString(2, "test_hash");
            statement.setString(3, "cart@test.com");

            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Test user could not be created."
        );
    }
    private int insertTestProduct() throws SQLException {

        String sql = """
            INSERT INTO products
                (product_name, price, stock_quantity)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, "Test Shirt");
            statement.setBigDecimal(
                    2,
                    new java.math.BigDecimal("19.99")
            );
            statement.setInt(3, 10);

            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Test product could not be created."
        );
    }
}