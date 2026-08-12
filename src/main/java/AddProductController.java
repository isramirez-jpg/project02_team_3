import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Add Product scene.
 *
 * This controller allows an administrator to enter product information
 * and add a new product to the clothing catalog.
 *
 * @author Ha Nguyen
 * @date 8/12/2026
 */
public class AddProductController {

    @FXML
    private TextField productNameField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private TextField colorField;

    @FXML
    private ComboBox<String> sizeComboBox;

    @FXML
    private TextField stockQuantityField;

    @FXML
    private TextField imagePathField;

    @FXML
    private Label statusLabel;

    private Stage stage;
    private DatabaseManager databaseManager;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;


    /**
     * Initializes the Add Product screen.
     */
    @FXML
    public void initialize() {

        // Gender choices
        genderComboBox.getItems().addAll(
                "Men",
                "Women",
                "Unisex"
        );

        // Size choices
        sizeComboBox.getItems().addAll(
                "XS",
                "S",
                "M",
                "L",
                "XL",
                "XXL"
        );
    }


    /**
     * Receives application data from SceneFactory.
     *
     * @param stage the application stage
     * @param databaseManager the database manager
     */
    public void setApplicationData(
            Stage stage,
            DatabaseManager databaseManager
    ) {
        this.stage = stage;
        this.databaseManager = databaseManager;

        this.productDAO = new ProductDAO(databaseManager);
        this.categoryDAO = new CategoryDAO(databaseManager);

        loadCategories();
    }


    /**
     * Loads available categories into the category ComboBox.
     */
    private void loadCategories() {

        categoryComboBox.getItems().setAll(
                categoryDAO.getAll()
        );
    }


    /**
     * Adds a new product to the database.
     */
    @FXML
    private void handleAddProduct() {

        String productName =
                productNameField.getText().trim();

        Category selectedCategory =
                categoryComboBox.getValue();

        String description =
                descriptionField.getText().trim();

        String priceText =
                priceField.getText().trim();

        String gender =
                genderComboBox.getValue();

        String color =
                colorField.getText().trim();

        String size =
                sizeComboBox.getValue();

        String stockText =
                stockQuantityField.getText().trim();

        String imagePath =
                imagePathField.getText().trim();


        // Validate required fields
        if (productName.isEmpty()
                || selectedCategory == null
                || priceText.isEmpty()
                || gender == null
                || color.isEmpty()
                || size == null
                || stockText.isEmpty()) {

            showError(
                    "Missing Information",
                    "Please complete all required product fields."
            );
            return;
        }


        // Convert price
        double price;

        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {

            showError(
                    "Invalid Price",
                    "Please enter a valid price."
            );
            return;
        }


        // Validate price
        if (price < 0) {

            showError(
                    "Invalid Price",
                    "Price cannot be negative."
            );
            return;
        }


        // Convert stock quantity
        int stockQuantity;

        try {
            stockQuantity =
                    Integer.parseInt(stockText);
        } catch (NumberFormatException e) {

            showError(
                    "Invalid Stock Quantity",
                    "Stock quantity must be a whole number."
            );
            return;
        }


        // Validate stock quantity
        if (stockQuantity < 0) {

            showError(
                    "Invalid Stock Quantity",
                    "Stock quantity cannot be negative."
            );
            return;
        }


        /*
         * Create the Product object.
         *
         * The productId is not supplied because the database
         * should generate it automatically.
         */
        Product product = new Product(
                selectedCategory.getCategoryId(),
                productName,
                description,
                price,
                gender,
                color,
                size,
                stockQuantity,
                imagePath
        );


        // Insert product into database
        Product addedProduct = productDAO.insert(product);

        if (addedProduct != null) {

            showInformation(
                    "Product Added",
                    "The product was added successfully."
            );

            clearFields();

        } else {

            showError(
                    "Add Product Failed",
                    "The product could not be added."
            );
        }
    }


    /**
     * Clears all product input fields.
     */
    @FXML
    private void handleClear() {
        clearFields();
    }


    /**
     * Clears the form.
     */
    private void clearFields() {

        productNameField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
        priceField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        colorField.clear();
        sizeComboBox.getSelectionModel().clearSelection();
        stockQuantityField.clear();
        imagePathField.clear();

        statusLabel.setText("");
    }


    /**
     * Returns to the Catalog Management scene.
     */
    @FXML
    private void handleBack() {

        stage.setScene(
                SceneFactory.create(
                        SceneType.CATALOG_MANAGEMENT,
                        stage,
                        databaseManager
                )
        );
    }


    /**
     * Displays an error message.
     *
     * @param title alert title
     * @param message alert message
     */
    private void showError(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Displays an information message.
     *
     * @param title alert title
     * @param message alert message
     */
    private void showInformation(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}