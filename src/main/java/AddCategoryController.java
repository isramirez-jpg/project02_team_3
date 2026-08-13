import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Name: Ha Nguyen
 * Date: 8/4/2026
 * Explanation: This controller manages the Add Category scene.
 * It collects category information from the user and uses CategoryDAO
 * to insert a new category into the categories database table.
 */
public class AddCategoryController {
    private Stage stage;
    private DatabaseManager db;

    public void setNavigation(Stage stage, DatabaseManager db) {
        this.stage = stage;
        this.db = db;
    }

    @FXML
    private TextField categoryNameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label messageLabel;

    private CategoryDAO categoryDAO;

    /**
     * Sets the CategoryDAO used to save category information.
     */
    public void setCategoryDAO(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    /**
     * Saves a new category to the database.
     */
    @FXML
    private void handleSaveCategory() {

        String categoryName = categoryNameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (categoryName.isEmpty()) {
            messageLabel.setText("Category name is required.");
            return;
        }

        Category category = new Category(
                categoryName,
                description
        );

        Category savedCategory = categoryDAO.insert(category);

        if (savedCategory != null) {
            messageLabel.setText("Category added successfully.");

            categoryNameField.clear();
            descriptionArea.clear();
        } else {
            messageLabel.setText("Failed to add category.");
        }
    }

    /**
     * Clears the category input fields.
     */
    @FXML
    private void handleClear() {
        categoryNameField.clear();
        descriptionArea.clear();
        messageLabel.setText("");
    }

    /**
     * Back to Dashboard.
     */
    @FXML
    private void handleBack() {
        stage.setScene(
                SceneFactory.create(
                        SceneType.CATALOG_MANAGEMENT,
                        stage,
                        db
                )
        );
    }
}