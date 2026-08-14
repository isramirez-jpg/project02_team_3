import javafx.fxml.FXML;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;
import javafx.scene.control.Label;

/**
 * @author Isabel Ramirez
 * @created 8.3.26
 */
public class CartController {

    private Stage stage;
    private DatabaseManager db;
    private String username;
    private CartDao cartDao;
    private Cart activeCart;
    private List<CartItem> cartItems;
    @FXML
    private void initialize() {

        productColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("itemTotal")
        );
    }

    public void setApplicationData(
            Stage stage,
            DatabaseManager db,
            String username
    ) {
        this.stage = stage;
        this.db = db;
        this.username = username;

        loadCart();
    }

    private void loadCart() {
        int userId = db.getUserIdByUsername(username);

        if (userId == -1) {
            System.out.println("Unable to find user: " + username);
            return;
        }
        cartDao = new CartDao(db);
        try {
            activeCart = cartDao.getOrCreateActiveCart(userId);
            cartItems = cartDao.findItemsByCartId(
                    activeCart.getCartId()
            );
            cartTable.getItems().clear();
            cartTable.getItems().addAll(cartItems);

            BigDecimal subtotal = BigDecimal.ZERO;

            for (CartItem item : cartItems) {
                subtotal = subtotal.add(
                        item.getItemTotal()
                );
            }

            subtotalLabel.setText(
                    "Subtotal: $" + subtotal
            );
            System.out.println(
                    "Loaded cart " + activeCart.getCartId()
                    + " with " + cartItems.size() + " items."
            );
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @FXML
    private void increaseQuantity() {

        CartItem selectedItem =
                cartTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        try {
            cartDao.updateQuantity(
                    selectedItem.getCartItemId(),
                    selectedItem.getQuantity() + 1
            );
            cartDao.updateQuantity(
                    selectedItem.getCartItemId(),
                    selectedItem.getQuantity() + 1
            );

            cartStatusLabel.setText("");

            loadCart();

            loadCart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            cartStatusLabel.setText(e.getMessage());
            cartStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void decreaseQuantity() {

        CartItem selectedItem =
                cartTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        if (selectedItem.getQuantity() <= 1) {
            return;
        }

        try {
            cartDao.updateQuantity(
                    selectedItem.getCartItemId(),
                    selectedItem.getQuantity() - 1
            );

            loadCart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeSelectedItem() {

        CartItem selectedItem =
                cartTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        try {
            cartDao.removeItem(
                    selectedItem.getCartItemId()
            );

            loadCart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        stage.setScene(
                SceneFactory.create(
                        SceneType.MAIN,
                        stage,
                        db
                )
        );
    }
    @FXML
    private void goToCheckout() {
        stage.setScene(
                SceneFactory.create(
                        SceneType.CHECKOUT,
                        stage,
                        db
                )
        );
    }
    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> priceColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> totalColumn;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label cartStatusLabel;

}
