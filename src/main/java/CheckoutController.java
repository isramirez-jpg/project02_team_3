import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Controls the checkout scene and handles order placement.
 *
 * @author Isabel Ramirez
 * @created 8.11.26
 */
public class CheckoutController {

    private static final Logger LOGGER =
            Logger.getLogger(CheckoutController.class.getName());
    @FXML
    private Label statusLabel;

    private Stage stage;
    private DatabaseManager db;
    private int cartId;

    @FXML
    private Button placeOrderButton;

    /**
     * Receives the application dependencies and active cart ID.
     *
     * @param stage the application stage
     * @param db the database manager
     * @param cartId the active cart ID
     */
    public void setApplicationData(
            Stage stage,
            DatabaseManager db,
            int cartId
    ) {
        this.stage = stage;
        this.db = db;
        this.cartId = cartId;
    }

    /**
     * Places the current cart as an order.
     */
    @FXML
    private void handlePlaceOrder() {
        placeOrderButton.setDisable(true);

        OrderDao orderDao = new OrderDao(db);

        try {
            Order order = orderDao.checkoutCart(cartId);

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Order Complete");
            alert.setHeaderText("Order Placed Successfully");
            alert.setContentText(
                    "Order #" + order.getOrderId()
                            + " has been placed."
            );

            alert.showAndWait();

            stage.setScene(
                    SceneFactory.create(
                            SceneType.MAIN,
                            stage,
                            db
                    )
            );
        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Database error while checking out cart " + cartId,
                    e
            );

            statusLabel.setText(
                    "Checkout failed. Please try again."
            );
            statusLabel.setStyle("-fx-text-fill: red;");

            placeOrderButton.setDisable(false);

        } catch (IllegalStateException | IllegalArgumentException e) {

            LOGGER.log(
                    Level.WARNING,
                    "Checkout validation failed for cart " + cartId,
                    e
            );
            statusLabel.setText(
                    "Checkout could not be completed."
            );
            statusLabel.setStyle("-fx-text-fill: red;");

            placeOrderButton.setDisable(false);
        }
    }

    /**
     * Returns to the shopping cart.
     */
    @FXML
    private void handleBack() {
        stage.setScene(
                SceneFactory.create(
                        SceneType.CART,
                        stage,
                        db
                )
        );
    }

}