import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
     */
    public void setApplicationData(Stage stage,
                                   DatabaseManager db,
                                   Product product) {
        this.stage = stage;
        this.db = db;
        this.product = product;

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
            System.out.println("No image path for product: "
                    + product.getProductName());
            return;
        }

        try {
            // Make sure the resource path starts with "/"
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            var imageUrl =
                    getClass().getResource(imagePath);

            if (imageUrl == null) {
                System.out.println(
                        "Image not found: " + imagePath
                );
                return;
            }

            Image image = new Image(imageUrl.toExternalForm());

            productImageView.setImage(image);

        } catch (Exception e) {
            System.out.println(
                    "Unable to load product image: " + imagePath
            );
            e.printStackTrace();
        }
    }

    /**
     * Adds the selected product to the shopping cart.
     *
     * Cart functionality will be connected here.
     */
    @FXML
    private void handleAddToCart() {

        if (product == null) {
            return;
        }

        System.out.println(
                "Add to cart: " + product.getProductName()
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
}