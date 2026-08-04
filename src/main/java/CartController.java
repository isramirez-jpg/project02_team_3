import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * @author Isabel Ramirez
 * @created 8.3.26
 */
public class CartController {

    private Stage stage;
    private DatabaseManager db;

    public void setApplicationData(
            Stage stage,
            DatabaseManager db
    ) {
        this.stage = stage;
        this.db = db;
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
}