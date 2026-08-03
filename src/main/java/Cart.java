/**
 * @author Isabel Ramirez
 * @created 8.2.26
 *
 */
public class Cart {

    private int cartId;
    private int userId;
    private String status;

    /**
     * Creates a Cart object using values read from the database.
     *
     * @param cartId the cart's database ID
     * @param userId the ID of the user who owns the cart
     * @param status the cart status, such as ACTIVE or COMPLETED
     */
    public Cart(int cartId, int userId, String status) {
        this.cartId = cartId;
        this.userId = userId;
        this.status = status;
    }

    /**
     * Creates a new active cart before it has a database ID.
     *
     * The cart ID starts at 0 because SQLite will generate
     * the real ID when the cart is inserted.
     *
     * @param userId the ID of the user who owns the cart
     */
    public Cart(int userId) {
        this.cartId = 0;
        this.userId = userId;
        this.status = "ACTIVE";
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.equals("ACTIVE")
                && !status.equals("COMPLETED")) {
            throw new IllegalArgumentException(
                    "Status must be ACTIVE or COMPLETED."
            );
        }

        this.status = status;
    }

    /**
     * Returns true when the cart is currently active.
     *
     * @return true when the status is ACTIVE
     */
    public boolean isActive() {
        return status.equals("ACTIVE");
    }

    /**
     * Marks the cart as completed.
     */
    public void markCompleted() {
        status = "COMPLETED";
    }
}

