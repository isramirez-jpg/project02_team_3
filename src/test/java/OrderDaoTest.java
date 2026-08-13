import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Isabel Ramirez
 * @created 8.13.26
 * Description: An LLM was used to assist with drafting test cases
 */
public class OrderDaoTest {

    private Connection connection;
    private DatabaseManager db;
    private CartDao cartDao;
    private OrderDao orderDao;

    @BeforeEach
    void setUp() throws SQLException {

        connection =
                CartCheckoutTestDatabase.createConnection();

        db = new DatabaseManager(connection, false);

        cartDao = new CartDao(db);
        orderDao = new OrderDao(db);
    }

    @AfterEach
    void tearDown() throws SQLException {

        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void checkoutCartCreatesCompletedOrder()
            throws SQLException {

        int userId = insertTestUser();
        int productId = insertTestProduct();

        Cart cart = cartDao.create(userId);

        cartDao.addProduct(
                cart.getCartId(),
                productId,
                2
        );

        Order order =
                orderDao.checkoutCart(
                        cart.getCartId()
                );

        assertTrue(order.getOrderId() > 0);
        assertEquals(userId, order.getUserId());
        assertEquals(
                new BigDecimal("39.98"),
                order.getTotalAmount()
        );
        assertEquals(
                "COMPLETED",
                order.getStatus()
        );

        Cart completedCart =
                cartDao.findById(
                        cart.getCartId()
                ).orElseThrow();

        assertFalse(completedCart.isActive());
        assertEquals(
                "COMPLETED",
                completedCart.getStatus()
        );
    }
    @Test
    void checkoutCopiesCartItemsIntoOrder()
            throws SQLException {

        int userId = insertTestUser();
        int productId = insertTestProduct();

        Cart cart = cartDao.create(userId);

        cartDao.addProduct(
                cart.getCartId(),
                productId,
                2
        );

        Order order =
                orderDao.checkoutCart(cart.getCartId());

        List<OrderItem> items =
                orderDao.findItemsByOrderId(
                        order.getOrderId()
                );

        assertEquals(1, items.size());
        assertEquals(productId, items.get(0).getProductId());
        assertEquals(2, items.get(0).getQuantity());
        assertEquals(
                new BigDecimal("19.99"),
                items.get(0).getUnitPrice()
        );
    }

    @Test
    void checkoutEmptyCartThrowsException()
            throws SQLException {

        int userId = insertTestUser();

        Cart cart = cartDao.create(userId);

        assertThrows(
                IllegalStateException.class,
                () -> orderDao.checkoutCart(
                        cart.getCartId()
                )
        );
    }

    @Test
    void completedCartCannotBeCheckedOutAgain()
            throws SQLException {

        int userId = insertTestUser();
        int productId = insertTestProduct();

        Cart cart = cartDao.create(userId);

        cartDao.addProduct(
                cart.getCartId(),
                productId,
                1
        );

        orderDao.checkoutCart(cart.getCartId());

        assertThrows(
                SQLException.class,
                () -> orderDao.checkoutCart(
                        cart.getCartId()
                )
        );
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

            statement.setString(1, "order_test_user");
            statement.setString(2, "test_hash");
            statement.setString(3, "order@test.com");

            statement.executeUpdate();

            try (var keys =
                         statement.getGeneratedKeys()) {

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

            statement.setString(1, "Checkout Shirt");
            statement.setBigDecimal(
                    2,
                    new BigDecimal("19.99")
            );
            statement.setInt(3, 10);

            statement.executeUpdate();

            try (var keys =
                         statement.getGeneratedKeys()) {

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