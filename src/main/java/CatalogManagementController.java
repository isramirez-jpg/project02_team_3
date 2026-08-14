import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.Optional;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * Controller for the Catalog Management scene.
 *
 * This controller manages products and categories using two
 * TableView components. It uses ProductDAO and CategoryDAO
 * to perform database operations.
 *
 * @author Ha Nguyen
 * @date 8/12/2026
 */
public class CatalogManagementController {

    // Product Table

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Integer> productCategoryIdColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> genderColumn;

    @FXML
    private TableColumn<Product, String> colorColumn;

    @FXML
    private TableColumn<Product, String> sizeColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TableColumn<Product, String> imagePathColumn;

    // Category Table

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Number> categoryIdColumn;

    @FXML
    private TableColumn<Category, String> categoryNameColumn;

    @FXML
    private TableColumn<Category, String> categoryDescriptionColumn;


    // Database

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    private Stage stage;
    private DatabaseManager databaseManager;


    /**
     * Initializes the TableView column bindings.
     */
    @FXML
    public void initialize() {

        // Allow product cells to be edited directly.
        productTable.setEditable(true);

        // Product columns
        productIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getProductId()
                ).asObject()
        );

        productNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getProductName()
                )
        );

        productCategoryIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCategoryId()
                ).asObject()
        );

        descriptionColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getDescription()
                )
        );

        priceColumn.setCellValueFactory(
                data -> new SimpleDoubleProperty(
                        data.getValue().getPrice()
                ).asObject()
        );

        genderColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getGender()
                )
        );

        colorColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getColor()
                )
        );

        sizeColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getSize()
                )
        );

        stockColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getStockQuantity()
                ).asObject()
        );

        imagePathColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getImagePath()
                )
        );

        // Make Product Name editable
        productNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        productNameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setProductName(event.getNewValue());

            saveProduct(product);
        });

        // Make Category ID editable
        productCategoryIdColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new IntegerStringConverter()
                )
        );

        productCategoryIdColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setCategoryId(event.getNewValue());

            saveProduct(product);
        });

        // Make Description editable
        descriptionColumn.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        descriptionColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setDescription(event.getNewValue());

            saveProduct(product);
        });


        // Make Price editable
        priceColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new DoubleStringConverter()
                )
        );

        priceColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setPrice(event.getNewValue());

            saveProduct(product);
        });

        // Make Gender editable
        genderColumn.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        genderColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setGender(event.getNewValue());

            saveProduct(product);
        });


        // Make Color editable
        colorColumn.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        colorColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setColor(event.getNewValue());

            saveProduct(product);
        });

        // Make Size editable
        sizeColumn.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        sizeColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setSize(event.getNewValue());

            saveProduct(product);
        });


        // Make Stock editable
        stockColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new IntegerStringConverter()
                )
        );

        stockColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setStockQuantity(event.getNewValue());

            saveProduct(product);
        });

        // Make Image Path editable
        imagePathColumn.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        imagePathColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();

            product.setImagePath(event.getNewValue());

            saveProduct(product);
        });

        // Category columns
        categoryIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCategoryId()
                )
        );

        categoryNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCategoryName()
                )
        );

        categoryDescriptionColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getDescription()
                )
        );
    }

    /**
     * Saves an edited product to the database.
     *
     */
    private void saveProduct(Product product) {

        boolean updated = productDAO.update(product);

        if (updated) {
            productTable.refresh();
        } else {
            showError(
                    "Update Failed",
                    "The product could not be updated."
            );

            loadProducts();
        }
    }

    /**
     * Receives the application data from SceneFactory.
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

        loadProducts();
        loadCategories();
    }


    /**
     * Loads all products into the product TableView.
     */
    private void loadProducts() {
        productTable.getItems().setAll(
                productDAO.getAll()
        );
    }


    /**
     * Loads all categories into the category TableView.
     */
    private void loadCategories() {
        categoryTable.getItems().setAll(
                categoryDAO.getAll()
        );
    }


    /**
     * Refreshes the product TableView.
     */
    @FXML
    private void handleRefreshProducts() {
        loadProducts();
    }


    /**
     * Refreshes the category TableView.
     */
    @FXML
    private void handleRefreshCategories() {
        loadCategories();
    }


    /**
     * Deletes the selected product after confirmation.
     */
    @FXML
    private void handleDeleteProduct() {

        Product selectedProduct =
                productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showWarning(
                    "No Product Selected",
                    "Please select a product to delete."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Product");
        confirmation.setHeaderText(
                "Delete " + selectedProduct.getProductName() + "?"
        );
        confirmation.setContentText(
                "This action cannot be undone."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            boolean deleted =
                    productDAO.delete(
                            selectedProduct.getProductId()
                    );

            if (deleted) {
                loadProducts();

                showInformation(
                        "Product Deleted",
                        "The product was deleted successfully."
                );
            } else {
                showError(
                        "Delete Failed",
                        "The product could not be deleted."
                );
            }
        }
    }


    /**
     * Deletes the selected category after confirmation.
     */
    @FXML
    private void handleDeleteCategory() {

        Category selectedCategory =
                categoryTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showWarning(
                    "No Category Selected",
                    "Please select a category to delete."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Category");
        confirmation.setHeaderText(
                "Delete "
                        + selectedCategory.getCategoryName()
                        + "?"
        );

        confirmation.setContentText(
                "This action cannot be undone."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            if (categoryDAO.hasProducts(selectedCategory.getCategoryId())) {
                showWarning(
                        "Category In Use",
                        "This category cannot be deleted because products are assigned to it."
                );
                return;
            }

            boolean deleted =
                    categoryDAO.delete(
                            selectedCategory.getCategoryId()
                    );

            if (deleted) {
                loadCategories();

                showInformation(
                        "Category Deleted",
                        "The category was deleted successfully."
                );
            } else {
                showError(
                        "Delete Failed",
                        "The category could not be deleted."
                );
            }
        }
    }


    /**
     * Placeholder for adding a product.
     */
    @FXML
    private void handleAddProduct() {

        stage.setScene(
                SceneFactory.create(
                        SceneType.ADD_PRODUCT,
                        stage,
                        databaseManager
                )
        );
    }

    /**
     * Placeholder for adding a category.
     */
    @FXML
    private void handleAddCategory() {
        stage.setScene(
                SceneFactory.create(
                        SceneType.ADD_CATEGORY,
                        stage,
                        databaseManager
                )
        );
    }


    /**
     * Placeholder for editing a category.
     */
    @FXML
    private void handleEditCategory() {

        showInformation(
                "Edit Category",
                "Edit Category functionality will be added next."
        );
    }


    /**
     * Returns to the main scene.
     */
    @FXML
    private void handleBack() {

        stage.setScene(
                SceneFactory.create(
                        SceneType.MAIN,
                        stage,
                        databaseManager
                )
        );
    }


    // Alert Helpers

    private void showWarning(
            String title,
            String message
    ) {
        Alert alert = new Alert(
                Alert.AlertType.WARNING
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showInformation(
            String title,
            String message
    ) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showError(
            String title,
            String message
    ) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}