import javafx.application.Application;
import javafx.stage.Stage;
// Add Image import to load application icon
import javafx.scene.image.Image;

/**
 * Main's only jobs: open the database, show the first scene close the database.
 * All scene construction is delegated to SceneFactory.
 *
 *  @author Ha Nguyen
 *  @author Isabel Ramirez
 *  @author Miguel Quezada
 *  @version 0.1.0
 *  @since 2026-08-02
 *
 */
public class Main extends Application {

  private DatabaseManager db;

  @Override
  public void start(Stage stage) {
    // opens and creates the sqlite database called app.db
    db = new DatabaseManager();
    stage.setTitle("Cache Me Outside Clothing Co.");

    // set the team logo on the application window title bar and taskbar
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/team-logo.png")));

    // launch the LOGIN scene
    stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
    stage.show();
  }

  @Override
  public void stop() {
    // This is called automatically on window close
    if (db != null) db.close();
  }

  public static void main(String[] args) {
    // Launches the JavaFX application lifecycle
    launch(args);
  }

}