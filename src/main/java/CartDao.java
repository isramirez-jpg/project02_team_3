import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Isabel Ramirez
 * @created 8.3.26
 *
 */

public class CartDao {

    private final DatabaseManager db;

    public CartDao(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Creates a new shopping cart for a user.
     *
     * @param userId the owner's user ID
     * @return the created Cart
     * @throws SQLException if the insert fails
     */

    public Cart create(int userId) throws SQLException {
        String sql = """
                INSERT INTO carts (user_id, status)
                VALUES (?, 'ACTIVE')
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(1, userId);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Cart(
                            keys.getInt(1),
                            userId,
                            "ACTIVE"
                    );
                }
            }
        }

        throw new SQLException("Cart could not be created.");
    }

    public Optional<Cart> findById(int cartId)
            throws SQLException {

        String sql = """
                SELECT cart_id, user_id, status
                FROM carts
                WHERE cart_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, cartId);

            try (ResultSet results =
                         statement.executeQuery()) {

                if (results.next()) {
                    return Optional.of(new Cart(
                            results.getInt("cart_id"),
                            results.getInt("user_id"),
                            results.getString("status")
                    ));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Cart> findActiveByUserId(int userId)
            throws SQLException {

        String sql = """
                SELECT cart_id, user_id, status
                FROM carts
                WHERE user_id = ?
                  AND status = 'ACTIVE'
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet results =
                         statement.executeQuery()) {

                if (results.next()) {
                    return Optional.of(new Cart(
                            results.getInt("cart_id"),
                            results.getInt("user_id"),
                            results.getString("status")
                    ));
                }
            }
        }

        return Optional.empty();
    }

    public Cart getOrCreateActiveCart(int userId)
            throws SQLException {

        Optional<Cart> existing =
                findActiveByUserId(userId);

        if (existing.isPresent()) {
            return existing.get();
        }

        return create(userId);
    }

    public CartItem addProduct(
            int cartId,
            int productId,
            int quantity
    ) throws SQLException {

        validateQuantity(quantity);

        Optional<CartItem> existing =
                findItem(cartId, productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQuantity =
                    item.getQuantity() + quantity;
            ProductInformation product =
                    findProduct(productId);

            if (newQuantity > product.stockQuantity) {
                throw new IllegalArgumentException(
                        "Only "
                                + product.stockQuantity
                                + " of "
                                + product.productName
                                + " are available."
                );
            }

            updateQuantity(
                    item.getCartItemId(),
                    newQuantity
            );

            item.setQuantity(newQuantity);
            return item;
        }

        ProductInformation product =
                findProduct(productId);
        if (quantity > product.stockQuantity) {
            throw new IllegalArgumentException(
                    "Only "
                            + product.stockQuantity
                            + " of "
                            + product.productName
                            + " are available."
            );
        }

        String sql = """
                INSERT INTO cart_items
                    (cart_id, product_id, quantity, unit_price)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(1, cartId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.setBigDecimal(
                    4,
                    product.unitPrice
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return new CartItem(
                            keys.getInt(1),
                            cartId,
                            productId,
                            product.productName,
                            product.unitPrice,
                            quantity
                    );
                }
            }
        }

        throw new SQLException(
                "Cart item could not be created."
        );
    }

    public List<CartItem> findItemsByCartId(
            int cartId
    ) throws SQLException {

        String sql = """
                SELECT ci.cart_item_id,
                       ci.cart_id,
                       ci.product_id,
                       p.product_name,
                       ci.unit_price,
                       ci.quantity
                FROM cart_items ci
                JOIN products p
                  ON ci.product_id = p.product_id
                WHERE ci.cart_id = ?
                ORDER BY ci.cart_item_id
                """;

        List<CartItem> items = new ArrayList<>();

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, cartId);

            try (ResultSet results =
                         statement.executeQuery()) {

                while (results.next()) {
                    items.add(new CartItem(
                            results.getInt("cart_item_id"),
                            results.getInt("cart_id"),
                            results.getInt("product_id"),
                            results.getString("product_name"),
                            results.getBigDecimal("unit_price"),
                            results.getInt("quantity")
                    ));
                }
            }
        }

        return items;
    }

    public boolean updateQuantity(
            int cartItemId,
            int quantity
    ) throws SQLException {

        validateQuantity(quantity);
        String stockSql = """
        SELECT p.product_name,
               p.stock_quantity
        FROM cart_items ci
        JOIN products p
          ON ci.product_id = p.product_id
        WHERE ci.cart_item_id = ?
        """;

        try (PreparedStatement stockStatement =
                     db.getConnection().prepareStatement(stockSql)) {

            stockStatement.setInt(1, cartItemId);

            try (ResultSet results =
                         stockStatement.executeQuery()) {

                if (results.next()) {

                    int stockQuantity =
                            results.getInt("stock_quantity");

                    String productName =
                            results.getString("product_name");

                    if (quantity > stockQuantity) {
                        throw new IllegalArgumentException(
                                "Only "
                                        + stockQuantity
                                        + " of "
                                        + productName
                                        + " are available."
                        );
                    }
                }
            }
        }

        String sql = """
                UPDATE cart_items
                SET quantity = ?
                WHERE cart_item_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, cartItemId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean removeItem(int cartItemId)
            throws SQLException {

        String sql = """
                DELETE FROM cart_items
                WHERE cart_item_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, cartItemId);
            return statement.executeUpdate() == 1;
        }
    }

    public int clearCart(int cartId)
            throws SQLException {

        String sql = """
                DELETE FROM cart_items
                WHERE cart_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, cartId);
            return statement.executeUpdate();
        }
    }

    public boolean updateStatus(
            int cartId,
            String status
    ) throws SQLException {

        String sql = """
        UPDATE carts
        SET status = ?,
            updated_at = CURRENT_TIMESTAMP
        WHERE cart_id = ?
        """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, cartId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteCart(int cartId)
            throws SQLException {

        String sql = """
                DELETE FROM carts
                WHERE cart_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, cartId);
            return statement.executeUpdate() == 1;
        }
    }

    private Optional<CartItem> findItem(
            int cartId,
            int productId
    ) throws SQLException {

        String sql = """
                SELECT ci.cart_item_id,
                       ci.cart_id,
                       ci.product_id,
                       p.product_name,
                       ci.unit_price,
                       ci.quantity
                FROM cart_items ci
                JOIN products p
                  ON ci.product_id = p.product_id
                WHERE ci.cart_id = ?
                  AND ci.product_id = ?
                """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, cartId);
            statement.setInt(2, productId);

            try (ResultSet results =
                         statement.executeQuery()) {

                if (results.next()) {
                    return Optional.of(new CartItem(
                            results.getInt("cart_item_id"),
                            results.getInt("cart_id"),
                            results.getInt("product_id"),
                            results.getString("product_name"),
                            results.getBigDecimal("unit_price"),
                            results.getInt("quantity")
                    ));
                }
            }
        }

        return Optional.empty();
    }

    private ProductInformation findProduct(int productId)
            throws SQLException {

        String sql = """
        SELECT product_name,
               price,
               stock_quantity
        FROM products
        WHERE product_id = ?
        """;

        try (PreparedStatement statement =
                     db.getConnection().prepareStatement(sql)) {

            statement.setInt(1, productId);

            try (ResultSet results =
                         statement.executeQuery()) {

                if (results.next()) {
                    return new ProductInformation(
                            results.getString("product_name"),
                            results.getBigDecimal("price"),
                            results.getInt("stock_quantity")
                    );
                }
            }
        }

        throw new SQLException(
                "Product does not exist: " + productId
        );
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least one."
            );
        }
    }

    private static class ProductInformation {

        private final String productName;
        private final BigDecimal unitPrice;
        private final int stockQuantity;

        private ProductInformation(
                String productName,
                BigDecimal unitPrice,
                int stockQuantity
        ) {
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.stockQuantity = stockQuantity;
        }
    }
}