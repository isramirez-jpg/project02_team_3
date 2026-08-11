import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides database operations for orders and order items.
 *
 * @author Isabel Ramirez
 * @created 8.10.26
 */
public class OrderDao {

    private final DatabaseManager db;

    public OrderDao(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Creates a new pending order for a user.
     *
     * @param userId the ID of the user placing the order
     * @param totalAmount the total amount of the order
     * @return the newly created Order
     * @throws SQLException if the insert fails
     */
    public Order createOrder(
            int userId,
            BigDecimal totalAmount
    ) throws SQLException {

        String sql = """
                INSERT INTO orders
                    (user_id, total_amount, status)
                VALUES (?, ?, 'PENDING')
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(1, userId);
            statement.setBigDecimal(2, totalAmount);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Order(
                            keys.getInt(1),
                            userId,
                            totalAmount,
                            "PENDING",
                            null
                    );
                }
            }
        }

        throw new SQLException("Order could not be created.");
    }

    /**
     * Adds one purchased product to an order.
     */
    public OrderItem addOrderItem(
            int orderId,
            CartItem cartItem
    ) throws SQLException {

        String sql = """
                INSERT INTO order_items
                    (order_id, product_id, quantity, unit_price)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(1, orderId);
            statement.setInt(2, cartItem.getProductId());
            statement.setInt(3, cartItem.getQuantity());
            statement.setBigDecimal(
                    4,
                    cartItem.getUnitPrice()
            );

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new OrderItem(
                            keys.getInt(1),
                            orderId,
                            cartItem.getProductId(),
                            cartItem.getProductName(),
                            cartItem.getUnitPrice(),
                            cartItem.getQuantity()
                    );
                }
            }
        }

        throw new SQLException("Order item could not be created.");
    }

    /**
     * Finds an order by its database ID.
     */
    public Optional<Order> findById(int orderId)
            throws SQLException {

        String sql = """
                SELECT order_id,
                       user_id,
                       total_amount,
                       status,
                       created_at
                FROM orders
                WHERE order_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, orderId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return Optional.of(
                            new Order(
                                    results.getInt("order_id"),
                                    results.getInt("user_id"),
                                    results.getBigDecimal("total_amount"),
                                    results.getString("status"),
                                    results.getString("created_at")
                            )
                    );
                }
            }
        }

        return Optional.empty();
    }
}