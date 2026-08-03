import java.math.BigDecimal;

/**
 * @author Isabel Ramirez
 * @created 8.2.26
 *
 */

public class CartItem {

    private int cartItemId;
    private int cartId;
    private int productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;

    /**
     * Creates a CartItem using values read from the database.
     *
     * @param cartItemId the cart item's database ID
     * @param cartId the ID of the cart containing this item
     * @param productId the product's database ID
     * @param productName the product name shown in the cart
     * @param unitPrice the saved price for one product
     * @param quantity the number of products in the cart
     */
    public CartItem(
            int cartItemId,
            int cartId,
            int productId,
            String productName,
            BigDecimal unitPrice,
            int quantity
    ) {
        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least one."
            );
        }

        if (unitPrice == null
                || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Unit price cannot be negative."
            );
        }

        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * Creates a new CartItem before SQLite assigns its ID.
     */
    public CartItem(
            int cartId,
            int productId,
            String productName,
            BigDecimal unitPrice,
            int quantity
    ) {
        this(
                0,
                cartId,
                productId,
                productName,
                unitPrice,
                quantity
        );
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCartId() {
        return cartId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least one."
            );
        }

        this.quantity = quantity;
    }

    /**
     * Calculates unit price multiplied by quantity.
     *
     * @return the total price for this cart item
     */
    public BigDecimal getItemTotal() {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }
}