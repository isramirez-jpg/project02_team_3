import javafx.fxml.FXML;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.control.ListView;

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
            cartListView.getItems().clear();

            if (cartItems.isEmpty()) {
                cartListView.getItems().add("Your cart is currently empty.");
            } else {
                for (CartItem item : cartItems) {
                    cartListView.getItems().add(
                            item.getProductName()
                                    + " x"
                                    + item.getQuantity()
                    );
                }
            }
            System.out.println(
                    "Loaded cart " + activeCart.getCartId()
                    + " with " + cartItems.size() + " items."
            );
        } catch (SQLException e) { e.printStackTrace(); }
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
    private ListView<String> cartListView;
}