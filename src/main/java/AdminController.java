import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * The AdminController class is responsible for managing the
 * admin view in the application.
 * It handles the display of user information in a table
 * and provides navigation back to the main scene.
 *
 *  @author Miguel Quezada
 *  @version 0.1.0
 *  @since 2026-08-02
 *
 */
public class AdminController {

  // Look at the FXML file called AdminScene.fxml, find the element with id equal to usersTable,
  // and automatically connect it to this Java variable called usersTable.
  @FXML private TableView<UserInfo> usersTable;
  @FXML private Button backButton;

  // Saved a reference to the primary application Stage to allow scene switching
  private Stage stage;
  // Saved a reference to the application DatabaseManager
  private DatabaseManager db;

  /**
   * Receives external dependencies from the SceneFactory and populates the users table.
   *
   * @param stage the Stage object for the application window
   * @param db the DatabaseManager instance
   */
  public void initData(Stage stage, DatabaseManager db) {
    this.stage = stage;
    this.db = db;
    usersTable.setItems(FXCollections.observableArrayList(db.getAllUsersForAdmin()));
  }

  /**
   * Handles the action event when the back button is clicked.
   * It switches the primary stage back to the MAIN scene.
   */
  @FXML
  private void handleBack() {
    stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db));
  }
}