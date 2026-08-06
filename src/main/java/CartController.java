import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;


/**
 * @author Isabel Ramirez
 * @created 8.3.26
 */

public class CartController {

    private Stage stage;
    private DatabaseManager db;
    private CartDao cartDao;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, java.math.BigDecimal> priceColumn;

    @FXML
    private TableColumn<CartItem, java.math.BigDecimal> totalColumn;

    @FXML
    private Label subtotalLabel;

    private final ObservableList<CartItem> cartItems =
            FXCollections.observableArrayList();

    public void setApplicationData(
            Stage stage,
            DatabaseManager db
    ) {
        this.stage = stage;
        this.db = db;

        cartDao = new CartDao(db);

        initializeTable();
        loadCart();
    }

    private void initializeTable() {

        productColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice"));

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("itemTotal"));

        cartTable.setItems(cartItems);

        subtotalLabel.setText("Subtotal: $0.00");

    }
    private void loadCart() {

        try {

            Cart cart =
                    cartDao.getOrCreateActiveCart(1);

            cartItems.setAll(
                    cartDao.findItemsByCartId(
                            cart.getCartId()
                    )
            );

            updateSubtotal();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
    private void updateSubtotal() {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            subtotal = subtotal.add(
                    item.getItemTotal()
            );

        }

        subtotalLabel.setText(
                "Subtotal: $" + subtotal
        );

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
    private void removeSelected() {

        CartItem selected =
                cartTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null)
            return;

        cartItems.remove(selected);

        updateSubtotal();

    }

    @FXML
    private void openCheckout() {

        System.out.println("Checkout clicked.");

    }
}