import java.math.BigDecimal;

/**
 * Represents a completed or processing customer order.
 *
 * @author Isabel Ramirez
 * @created 8.8.26
 */
public class Order {

    private int orderId;
    private int userId;
    private BigDecimal totalAmount;
    private String status;
    private String createdAt;

    /**
     * Creates an Order object using values read from the database.
     *
     * @param orderId the order's database ID
     * @param userId the ID of the user who placed the order
     * @param totalAmount the total cost of the order
     * @param status the current order status
     * @param createdAt the date and time the order was created
     */
    public Order(
            int orderId,
            int userId,
            BigDecimal totalAmount,
            String status,
            String createdAt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        setTotalAmount(totalAmount);
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Creates a new order before SQLite assigns its database ID.
     *
     * @param userId the ID of the user placing the order
     * @param totalAmount the total cost of the order
     */
    public Order(
            int userId,
            BigDecimal totalAmount
    ) {
        this.orderId = 0;
        this.userId = userId;
        setTotalAmount(totalAmount);
        this.status = "PENDING";
        this.createdAt = null;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null
                || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total amount cannot be negative."
            );
        }

        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.equals("PENDING")
                && !status.equals("COMPLETED")
                && !status.equals("CANCELLED")) {
            throw new IllegalArgumentException(
                    "Status must be PENDING, COMPLETED, or CANCELLED."
            );
        }

        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}