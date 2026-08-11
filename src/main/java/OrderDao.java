import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


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
        if (totalAmount == null
                || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total amount cannot be negative."
            );
        }

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
    /**
     * Retrieves all orders belonging to a user.
     *
     * @param userId the ID of the user
     * @return a list of the user's orders
     * @throws SQLException if the query fails
     */
    public List<Order> findByUserId(int userId)
            throws SQLException {

        List<Order> orders = new ArrayList<>();

        String sql = """
            SELECT order_id,
                   user_id,
                   total_amount,
                   status,
                   created_at
            FROM orders
            WHERE user_id = ?
            ORDER BY created_at DESC
            """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    orders.add(
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

        return orders;
    }
    /**
     * Retrieves all items that belong to an order.
     *
     * @param orderId the ID of the order
     * @return a list of OrderItem objects for the order
     * @throws SQLException if the query fails
     */
    public List<OrderItem> findItemsByOrderId(int orderId)
            throws SQLException {

        List<OrderItem> items = new ArrayList<>();

        String sql = """
            SELECT oi.order_item_id,
                   oi.order_id,
                   oi.product_id,
                   p.product_name,
                   oi.unit_price,
                   oi.quantity
            FROM order_items oi
            JOIN products p
              ON oi.product_id = p.product_id
            WHERE oi.order_id = ?
            ORDER BY oi.order_item_id
            """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, orderId);

            try (ResultSet results = statement.executeQuery()) {

                while (results.next()) {

                    items.add(
                            new OrderItem(
                                    results.getInt("order_item_id"),
                                    results.getInt("order_id"),
                                    results.getInt("product_id"),
                                    results.getString("product_name"),
                                    results.getBigDecimal("unit_price"),
                                    results.getInt("quantity")
                            )
                    );
                }
            }
        }

        return items;
    }

    /**
     * Converts an active shopping cart into a completed order.
     *
     * The checkout is performed as one database transaction.
     * If any step fails, all database changes are rolled back.
     *
     * @param cartId the ID of the cart being checked out
     * @return the completed Order
     * @throws SQLException if checkout cannot be completed
     */
    public Order checkoutCart(int cartId) throws SQLException {

        Connection connection = db.getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();

        CartDao cartDao = new CartDao(db);

        try {
            connection.setAutoCommit(false);

            // Find the cart
            Cart cart = cartDao.findById(cartId)
                    .orElseThrow(() ->
                            new SQLException(
                                    "Cart does not exist: " + cartId
                            )
                    );

            // Make sure the cart is still active
            if (!cart.isActive()) {
                throw new SQLException(
                        "Cart is not active: " + cartId
                );
            }

            // Load the cart items
            List<CartItem> cartItems =
                    cartDao.findItemsByCartId(cartId);

            // Prevent checkout of an empty cart
            if (cartItems.isEmpty()) {
                throw new SQLException(
                        "Cannot checkout an empty cart."
                );
            }

            // Calculate the order total
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem item : cartItems) {
                totalAmount = totalAmount.add(
                        item.getItemTotal()
                );
            }

            // Create the order
            Order order = createOrder(
                    cart.getUserId(),
                    totalAmount
            );

            // Copy each cart item into order_items
            for (CartItem item : cartItems) {
                addOrderItem(
                        order.getOrderId(),
                        item
                );
            }

            // Mark the order completed
            updateOrderStatus(
                    order.getOrderId(),
                    "COMPLETED"
            );

            order.setStatus("COMPLETED");

            // Mark the shopping cart completed
            boolean cartUpdated =
                    cartDao.updateStatus(
                            cartId,
                            "COMPLETED"
                    );

            if (!cartUpdated) {
                throw new SQLException(
                        "Cart status could not be updated."
                );
            }

            // Everything succeeded
            connection.commit();

            return order;

        } catch (SQLException | RuntimeException e) {

            connection.rollback();
            throw e;

        } finally {

            connection.setAutoCommit(originalAutoCommit);
        }
    }
    /**
     * Updates the status of an existing order.
     *
     * @param orderId the order's database ID
     * @param status the new order status
     * @throws SQLException if the update fails
     */
    private void updateOrderStatus(
            int orderId,
            String status
    ) throws SQLException {
        if (!status.equals("PENDING")
                && !status.equals("COMPLETED")
                && !status.equals("CANCELLED")) {
            throw new IllegalArgumentException(
                    "Invalid order status: " + status
            );
        }

        String sql = """
            UPDATE orders
            SET status = ?
            WHERE order_id = ?
            """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, orderId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated != 1) {
                throw new SQLException(
                        "Order status could not be updated."
                );
            }
        }
    }
}