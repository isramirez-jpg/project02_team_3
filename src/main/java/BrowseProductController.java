import import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

/**
 * Name: Ha Nguyen
 * Date: 8/8/2026
 *
 * Explanation:
 * This controller loads available products from the database through
 * ProductDAO and displays them in the browse-product user interface.
 * It also handles navigation back to the main scene.
 */
public class BrowseProductController {
    private Stage stage;
    private DatabaseManager db;
    private ProductDAO productDAO;

    @FXML
    private ListView<Product> productListView;

    /**
     * Sets the application data needed by this controller.
     */
    public void setApplicationData(Stage stage, DatabaseManager db) {
        this.stage = stage;
        this.db = db;
        this.productDAO = new ProductDAO(db);

        loadProducts();
    }

    /**
     * Loads all products from the database and displays them
     * in the product list.
     */
    private void loadProducts() {
        List<Product> products = productDAO.getAll();
        productListView.getItems().setAll(products);
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
