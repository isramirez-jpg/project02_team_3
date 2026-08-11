import java.math.BigDecimal;

/**
 * Represents one product included in a completed order.
 *
 * @author Isabel Ramirez
 * @created 8.8.26
 */
public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;

    /**
     * Creates an OrderItem using values read from the database.
     *
     * @param orderItemId the order item's database ID
     * @param orderId the ID of the order containing this item
     * @param productId the product's database ID
     * @param productName the product name
     * @param unitPrice the price of one product
     * @param quantity the quantity purchased
     */
    public OrderItem(
            int orderItemId,
            int orderId,
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

        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * Creates a new OrderItem before SQLite assigns its database ID.
     */
    public OrderItem(
            int orderId,
            int productId,
            String productName,
            BigDecimal unitPrice,
            int quantity
    ) {
        this(
                0,
                orderId,
                productId,
                productName,
                unitPrice,
                quantity
        );
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
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

    public BigDecimal getItemTotal() {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }
}