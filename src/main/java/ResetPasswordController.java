import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the Reset Password scene.
 *
 * This controller allows a user to reset their password by providing their username,
 * answering a security question, and setting a new password.
 *
 * @author Miguel Quezada
 * @date  2026-08-13
 */
public class ResetPasswordController {

  @FXML private TextField usernameField;
  @FXML private Label securityQuestionLabel;
  @FXML private TextField answerField;
  @FXML private PasswordField newPasswordField;
  @FXML private PasswordField confirmPasswordField;
  @FXML private Label statusLabel;
  @FXML private VBox step2Container;

  private Stage stage;
  private DatabaseManager db;
  private UserDAO userDAO;

  /**
   * Called by SceneFactory to setup stage, db and data access operations
   */
  public void setApplicationData(Stage stage, DatabaseManager db) {
    this.stage = stage;
    this.db = db;
    this.userDAO = db.getUserDAO();
  }

  /**
   * Retrieves the security question for the provided username.
   */
  @FXML
  private void handleFetchQuestion() {
    String username = usernameField.getText();
    if (username == null || username.trim().isEmpty()) {
      setStatus("Please enter your username.", true);
      return;
    }

    String question = userDAO.getSecurityQuestion(username);
    if (question != null) {
      securityQuestionLabel.setText(question);
      step2Container.setVisible(true);
      step2Container.setManaged(true);
      setStatus("Security question retrieved.", false);
    } else {
      setStatus("Username not found.", true);
      step2Container.setVisible(false);
      step2Container.setManaged(false);
    }
  }

  /**
   * Handles when the user wants to reset their password display messages on reset form
   */
  @FXML
  private void handleResetPassword() {
    String username = usernameField.getText();
    String answer = answerField.getText();
    String newPassword = newPasswordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    if (answer.trim().isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
      setStatus("All fields are required.", true);
      return;
    }

    if (!newPassword.equals(confirmPassword)) {
      setStatus("Passwords do not match.", true);
      return;
    }

    boolean success = userDAO.verifyAndResetPassword(username, answer, newPassword);
    if (success) {
      setStatus("Password reset successful! Redirecting...", false);
      stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
    } else {
      setStatus("Incorrect answer. Please try again.", true);
    }
  }

  /**
   * Handles the Cancel button click event. On cancel return to LOGIN scene.
   */
  @FXML
  private void handleCancel() {
    stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
  }

  /**
   * Updates the status label text
   */
  private void setStatus(String message, boolean isError) {
    statusLabel.setText(message);
    statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
  }
}