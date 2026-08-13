import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Name: Ha Nguyen
 * Date: 8/8/2026
 *
 * Explanation:
 * This controller loads available products from the database through
 * ProductDAO and displays them in the browse-product user interface.
 * It also handles searching and filtering products.
 */
public class BrowseProductController {
    private Stage stage;
    private DatabaseManager db;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, String> genderColumn;

    @FXML
    private TableColumn<Product, String> colorColumn;

    @FXML
    private TableColumn<Product, String> sizeColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> stockQuantityColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<String> sizeComboBox;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private ComboBox<String> categoryComboBox;

    /**
     * Sets the application data needed by this controller.
     */
    public void setApplicationData(Stage stage, DatabaseManager db) {
        this.stage = stage;
        this.db = db;
        this.productDAO = new ProductDAO(db);
        this.categoryDAO = new CategoryDAO(db);

        setupTable();
        setupFilters();
        loadProducts();
    }

    /**
     * Sets up the product table columns.
     */
    private void setupTable() {

        productNameColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getProductName()
                )
        );

        descriptionColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getDescription()
                )
        );

        genderColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getGender()
                )
        );

        colorColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getColor()
                )
        );

        sizeColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getSize()
                )
        );

        priceColumn.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(
                        cellData.getValue().getPrice()
                ).asObject()
        );

        stockQuantityColumn.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(
                        cellData.getValue().getStockQuantity()
                ).asObject()
        );
    }

    /**
     * Loads all products from the database and displays them
     * in the product table.
     */
    private void loadProducts() {
        List<Product> products = productDAO.getAll();
        productTableView.getItems().setAll(products);
    }

    /**
     * Sets up the product filter dropdowns.
     */
    private void setupFilters() {

        // Size filter
        sizeComboBox.getItems().clear();
        sizeComboBox.getItems().addAll(
                "All Sizes",
                "XS",
                "S",
                "M",
                "L",
                "XL",
                "XXL"
        );

        // Gender filter
        genderComboBox.getItems().clear();
        genderComboBox.getItems().addAll(
                "All Genders",
                "Men",
                "Women",
                "Unisex"
        );

        // Category filter
        categoryComboBox.getItems().clear();
        categoryComboBox.getItems().add("All Categories");

        List<Category> categories = categoryDAO.getAll();

        for (Category category : categories) {
            categoryComboBox.getItems().add(
                    category.getCategoryName()
            );
        }

        // Select default values
        sizeComboBox.getSelectionModel().selectFirst();
        genderComboBox.getSelectionModel().selectFirst();
        categoryComboBox.getSelectionModel().selectFirst();

        // Apply filters when user changes a selection
        sizeComboBox.setOnAction(event -> applyFilters());
        genderComboBox.setOnAction(event -> applyFilters());
        categoryComboBox.setOnAction(event -> applyFilters());
    }

    /**
     * Searches for products based on the text entered by the user.
     */
    @FXML
    private void handleSearch() {
        applyFilters();
    }

    /**
     * Applies the selected search and filter options.
     */
    private void applyFilters() {
        String searchText = searchTextField.getText()
                .trim()
                .toLowerCase();

        String selectedSize = sizeComboBox.getValue();
        String selectedGender = genderComboBox.getValue();
        String selectedCategory = categoryComboBox.getValue();

        List<Product> products = productDAO.getAll();

        List<Product> filteredProducts = products.stream()

                // Search by product name
                .filter(product ->
                        searchText.isEmpty()
                                || product.getProductName()
                                .toLowerCase()
                                .contains(searchText))

                // Filter by size
                .filter(product ->
                        selectedSize == null
                                || selectedSize.equals("All Sizes")
                                || product.getSize() != null
                                && product.getSize()
                                .equalsIgnoreCase(selectedSize))

                // Filter by gender
                .filter(product ->
                        selectedGender == null
                                || selectedGender.equals("All Genders")
                                || product.getGender() != null
                                && product.getGender()
                                .equalsIgnoreCase(selectedGender))

                // Filter by category
                .filter(product -> {
                    if (selectedCategory == null
                            || selectedCategory.equals("All Categories")) {
                        return true;
                    }

                    return categoriesMatch(
                            product.getCategoryId(),
                            selectedCategory
                    );
                })

                .collect(Collectors.toList());

        productTableView.getItems().setAll(filteredProducts);
    }

    /**
     * Checks whether the product category ID matches
     * the selected category name.
     */
    private boolean categoriesMatch(int productCategoryId,
                                    String selectedCategoryName) {

        List<Category> categories = categoryDAO.getAll();

        return categories.stream()
                .anyMatch(category ->
                        category.getCategoryId() == productCategoryId
                                && category.getCategoryName()
                                .equalsIgnoreCase(selectedCategoryName));
    }

    /**
     * Opens the product detail screen for the selected product.
     */
    @FXML
    private void handleViewDetails() {
        Product selectedProduct =
                productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            return;
        }

        // Product detail navigation will be added next.
    }

    /**
     * Return to the main scene.
     */
    @FXML
    private void handleBack() {
        stage.setScene(
                SceneFactory.create(SceneType.MAIN, stage, db)
        );
    }
}