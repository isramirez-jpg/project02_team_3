import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;
import java.util.List;
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
    @FXML
    private TableView<CartItem> checkoutTable;

    @FXML
    private TableColumn<CartItem, String> checkoutProductColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> checkoutPriceColumn;

    @FXML
    private TableColumn<CartItem, Integer> checkoutQuantityColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> checkoutTotalColumn;

    @FXML
    private Label checkoutSubtotalLabel;
    @FXML
    private void initialize() {

        checkoutProductColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );

        checkoutPriceColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice")
        );

        checkoutQuantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        checkoutTotalColumn.setCellValueFactory(
                new PropertyValueFactory<>("itemTotal")
        );
    }

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
        loadOrderSummary();
    }
    private void loadOrderSummary() {

        CartDao cartDao =
                new CartDao(db);

        try {

            List<CartItem> items =
                    cartDao.findItemsByCartId(cartId);

            checkoutTable.getItems().clear();
            checkoutTable.getItems().addAll(items);

            BigDecimal total =
                    BigDecimal.ZERO;

            for (CartItem item : items) {
                total = total.add(
                        item.getItemTotal()
                );
            }

            checkoutSubtotalLabel.setText(
                    "Order Total: $" + total
            );

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Unable to load checkout items",
                    e
            );

            statusLabel.setText(
                    "Unable to load order summary."
            );
        }
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
            statusLabel.setText(e.getMessage());
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