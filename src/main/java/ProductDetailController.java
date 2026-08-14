import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Name: Ha Nguyen
 * Date: 8/13/2026
 *
 * Explanation:
 * This controller displays detailed information for a selected product.
 * It receives a Product object from the Browse Product screen and
 * displays the product information and image.
 */
public class ProductDetailController {

    private Stage stage;
    private DatabaseManager db;
    private Product product;
    private String username;

    @FXML
    private ImageView productImageView;

    @FXML
    private Label productNameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label colorLabel;

    @FXML
    private Label sizeLabel;

    @FXML
    private Label stockLabel;

    @FXML
    private Button addToCartButton;

    /**
     * Sets the application data and selected product.
     *
     * @param stage the application stage
     * @param db the database manager
     * @param product the selected product
     * @param username the currently logged-in username
     */
    public void setApplicationData(
            Stage stage,
            DatabaseManager db,
            Product product,
            String username) {

        this.stage = stage;
        this.db = db;
        this.product = product;
        this.username = username;

        displayProduct();
    }

    /**
     * Displays the selected product information.
     */
    private void displayProduct() {

        if (product == null) {
            return;
        }

        productNameLabel.setText(product.getProductName());

        priceLabel.setText(
                String.format("$%.2f", product.getPrice())
        );

        descriptionLabel.setText(
                product.getDescription()
        );

        genderLabel.setText(
                "Gender: " + product.getGender()
        );

        colorLabel.setText(
                "Color: " + product.getColor()
        );

        sizeLabel.setText(
                "Size: " + product.getSize()
        );

        stockLabel.setText(
                "Stock: " + product.getStockQuantity()
        );

        loadProductImage();
    }

    /**
     * Loads the product image using the image path stored
     * in the Product object.
     */
    private void loadProductImage() {

        String imagePath = product.getImagePath();

        if (imagePath == null || imagePath.isBlank()) {
            System.out.println(
                    "No image path for product: "
                            + product.getProductName()
            );
            return;
        }

        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            var imageUrl = getClass().getResource(imagePath);

            if (imageUrl == null) {
                System.out.println(
                        "Image not found: " + imagePath
                );
                return;
            }

            Image image =
                    new Image(imageUrl.toExternalForm());

            productImageView.setImage(image);

        } catch (Exception e) {
            System.out.println(
                    "Unable to load product image: " + imagePath
            );
            e.printStackTrace();
        }
    }

    /**
     * Adds the selected product to the currently logged-in
     * user's active shopping cart.
     */
    @FXML
    private void handleAddToCart() {

        if (product == null) {
            return;
        }

        if (username == null || username.isBlank()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Login Required",
                    "Please log in before adding a product to your cart."
            );
            return;
        }

        try {
            int userId = db.getUserIdByUsername(username);

            if (userId == -1) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "User Error",
                        "Unable to find the current user."
                );
                return;
            }

            CartDao cartDao = new CartDao(db);

            // Get the user's existing active cart,
            // or create one if they do not have one.
            Cart activeCart =
                    cartDao.getOrCreateActiveCart(userId);

            // Add one of the selected product to the cart.
            cartDao.addProduct(
                    activeCart.getCartId(),
                    product.getProductId(),
                    1
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Added to Cart",
                    product.getProductName()
                            + " has been added to your cart."
            );

        } catch (SQLException e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Cart Error",
                    "Unable to add the product to your cart."
            );
        }
    }

    /**
     * Display Cart screen.
     */
    @FXML
    private void handleViewCart() {
        stage.setScene(
                SceneFactory.create(
                        SceneType.CART,
                        stage,
                        db
                )
        );
    }

    /**
     * Returns to the Browse Product screen.
     */
    @FXML
    private void handleBack() {

        stage.setScene(
                SceneFactory.create(
                        SceneType.BROWSE_PRODUCT,
                        stage,
                        db
                )
        );
    }

    /**
     * Displays an alert message.
     *
     * @param type alert type
     * @param title alert title
     * @param message alert message
     */
    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}