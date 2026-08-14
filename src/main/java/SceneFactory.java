import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Import the exception class thrown when an FXML file cant be found, read, or loaded
import java.io.IOException;
// Loads an object hierarchy from an XML document
import javafx.fxml.FXMLLoader;
// Import the base class for all JavaFX UI nodes that can contain children
import javafx.scene.Parent;
// Import the Image and ImageView classes to load and display images
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Builds and returns Scene objects on demand.
 * The Client calls create() with a SceneType.
 * The Factory knows how to build each scene - Main knows nothing about layouts.
 *
 * Note: DatabaseManager is threaded through every signature here.
 *
 *  @author Ha Nguyen
 *  @author Isabel Ramirez
 *  @author Miguel Quezada
 *  @version 0.1.0
 *  @since 2026-08-02
 */
public class SceneFactory {
  private static final Logger LOGGER =
      Logger.getLogger(SceneFactory.class.getName());

  //  Used to track active logged in user
  private static String currentUser = null;

  // Public entry point. Asks for a scene by type.
  public static Scene create(SceneType type, Stage stage, DatabaseManager db) {
    return switch (type) {
      case MAIN      -> buildMainScene(stage, db);
      case LOGIN     -> buildLoginScene(stage, db);
      // 08/13/2026 - MQ - Implement Reset Password
      case RESET_PASSWORD -> buildResetPasswordScene(stage, db);
      case ADMIN_TODO_LIST -> buildDashboardScene(stage, db);
      // Constructs the Admin UI view from its FXML layout
      // and initializes its controller
      case ADMIN_USER_DASHBOARD -> buildAdminScene(stage, db);
      case DASHBOARD -> buildDashboardScene(stage, db);
      case CART -> buildCartScene(stage, db);
      case ADD_CATEGORY -> buildAddCategoryScene(stage, db);
      case BROWSE_PRODUCT -> buildBrowseProduct(stage,db);
      case CATALOG_MANAGEMENT -> buildCatalogManagementScene(stage, db);
      case ADD_PRODUCT -> buildAddProductScene(stage, db);
      case CHECKOUT -> buildCheckoutScene(stage, db);
      // Product detail cannot be created without a selected Product.
      case PRODUCT_DETAIL -> throw new IllegalArgumentException(
              "A Product is required for PRODUCT_DETAIL."
      );
    };
  }

  /**
   * Creates a Scene for a selected Product.
   *
   * This overloaded method is used when a scene requires a Product
   * object, such as the Product Detail scene.
   *
   */
  public static Scene create(
          SceneType type,
          Stage stage,
          DatabaseManager db,
          Product product) {

    return switch (type) {
      case PRODUCT_DETAIL ->
              buildProductDetailScene(stage, db, product);

      default ->
              create(type, stage, db);
    };
  }

  //region Scene Builders

  /**
   * Builds the MAIN scene.
   *
   * @param stage the Stage object for the application window
   * @param db the DatabaseManager instance
   * @return a new Scene containing the main UI
   */
  private static Scene buildMainScene(Stage stage, DatabaseManager db) {

    // Fetch first and last name from database
    String fullName = (currentUser != null)
        ? db.getCustomerDAO().getCustomerNameByUsername(currentUser)
        : "Guest";
    Label loggedInLabel = new Label("User Currently Logged in:\n" + fullName);
    loggedInLabel.setStyle("-fx-font-weight: bold;");

    // Add Logout Button at top right
    Button logoutButton = new Button("Logout");
    logoutButton.setOnAction(e -> {
      // Clear active user session
      currentUser = null;
      stage.setScene(create(SceneType.LOGIN, stage, db));
    });

    // Layout top header with user name on left and logout on right
    BorderPane topHeader = new BorderPane();
    topHeader.setLeft(loggedInLabel);
    topHeader.setRight(logoutButton);
    topHeader.setPadding(new Insets(10));

    // Add title
    Label title = new Label("Welcome to Cache Me Outside Clothing Co.");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Button cartButton = new Button("Shopping Cart");
    // 08/13/2026 - MQ - Apply blue-button css class from master stylesheet
    cartButton.getStyleClass().add("blue-button");
    cartButton.setOnAction(e ->
        stage.setScene(create(SceneType.CART, stage, db))
    );

    Button browseProductButton = new Button("Browse Product");
    // 08/13/2026 - MQ - Apply blue-button css class from master stylesheet
    browseProductButton.getStyleClass().add("blue-button");
    browseProductButton.setOnAction(e ->
        stage.setScene(create(SceneType.BROWSE_PRODUCT, stage, db))
    );

    VBox centerLayout = new VBox(16, title, cartButton, browseProductButton);
    centerLayout.setAlignment(Pos.CENTER);

    //Only show Catalog Management button if the
    //current logged-in user has ADMIN role
    if (currentUser != null &&
            "ADMIN".equalsIgnoreCase(db.getUserDAO().getUserRole(currentUser))) {
      Button catalogManagementButton = new Button("Catalog Management");
      // 08/13/2026 - MQ - Apply blue-button css class from master stylesheet
      catalogManagementButton.getStyleClass().add("blue-button");
      catalogManagementButton.setOnAction(e ->
              stage.setScene(create(SceneType.CATALOG_MANAGEMENT, stage, db))
      );
      centerLayout.getChildren().add(catalogManagementButton);
    }

    // Only show the Admin To do List button if the
    // current logged-in user has ADMIN role
    if (currentUser != null && "ADMIN".equalsIgnoreCase(db.getUserDAO().getUserRole(currentUser))) {

      // create a button called managementTodoListButton
      Button managementTodoListButton = new Button("Admin Todo List");

      // 08/13/2026 - MQ - comment out old inline style
      //managementTodoListButton.setStyle("-fx-background-color: #146a9b; -fx-text-fill: white; -fx-font-weight: bold;");
      // 08/13/2026 - MQ - Apply blue-button css class from master stylesheet
      // to the Admin To Do List button
      managementTodoListButton.getStyleClass().add("blue-button");
      managementTodoListButton.setOnAction(e ->
          stage.setScene(create(SceneType.ADMIN_TODO_LIST, stage, db))
      );
      centerLayout.getChildren().add(managementTodoListButton);
    }

    // Only show the Admin User Dashboard button if the
    // current logged-in user has ADMIN role
    if (currentUser != null &&
        "ADMIN".equalsIgnoreCase(db.getUserDAO().getUserRole(currentUser))) {
      Button adminButton = new Button("Admin User Dashboard");
      // 08/13/2026 - MQ - comment out old inline style
      //adminButton.setStyle("-fx-background-color: #146a9b; -fx-text-fill: white; -fx-font-weight: bold;");
      // 08/13/2026 - MQ - Apply blue-button css class from master stylesheet
      // to the Admin User Dashboard button
      adminButton.getStyleClass().add("blue-button");
      adminButton.setOnAction(e -> stage.setScene(create(SceneType.ADMIN_USER_DASHBOARD, stage, db)));
      centerLayout.getChildren().add(adminButton);
    }

    BorderPane root = new BorderPane();
    root.setTop(topHeader);
    root.setCenter(centerLayout);

    // set scene width and height
    Scene scene = new Scene(root, 600, 450);

    // 08/13/2026 - MQ - add master stylesheet to MAIN scene
    scene.getStylesheets().add(
        SceneFactory.class.getResource("/styles.css").toExternalForm()
    );

    // Revise scene
    return scene;
  }

  /**
   * Builds the ADMIN scene.
   *
   * @param stage the Stage object for the application window
   * @param db the DatabaseManager instance
   * @return a new Scene containing the Admin UI
   */
  private static Scene buildAdminScene(Stage stage, DatabaseManager db) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(SceneFactory.class.getResource("/AdminScene.fxml"));
      Parent root = fxmlLoader.load();
      AdminController controller = fxmlLoader.getController();
      controller.initData(stage, db);
      return new Scene(root, 600, 450);
    } catch (IOException e) {
      e.printStackTrace();
      return buildMainScene(stage, db);
    }
  }

  /**
   * Builds the LOGIN scene, allowing users to log in or create a new account.
   *
   * @param stage  the Stage object for the application window
   * @param db  the DatabaseManager instance
   * @return a new Scene containing the login and registration UI
   */
  private static Scene buildLoginScene(Stage stage, DatabaseManager db) {

    // Load team logo image
    ImageView teamLogoImageView = new ImageView();
    try {
      // get the team logo from the resources folder
      Image teamLogoImage = new Image(SceneFactory.class.getResourceAsStream(
          "/images/team-logo.png"));
      teamLogoImageView.setImage(teamLogoImage);
      // set the width to 120 pixels
      teamLogoImageView.setFitWidth(120);
      teamLogoImageView.setPreserveRatio(true);
    } catch (Exception e) {
      // Catch exception and display a message in case the team logo is not found
      System.out.println("Unable to load team logo: " + e.getMessage());
    }

    Label title = new Label("Welcome. Please Log In");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    // Credentials Fields
    TextField usernameField = new TextField();
    usernameField.setPromptText("Username");

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Password");

    // 08/13/2026 - MQ - Assign explicit IDs to controls
    usernameField.setId("usernameField");
    passwordField.setId("passwordField");

    // These Fields are Hidden during login, but shown during registration
    TextField firstNameField = new TextField();
    firstNameField.setPromptText("First Name");

    TextField lastNameField = new TextField();
    lastNameField.setPromptText("Last Name");

    HBox nameRow = new HBox(8, firstNameField, lastNameField);
    HBox.setHgrow(firstNameField, Priority.ALWAYS);
    HBox.setHgrow(lastNameField, Priority.ALWAYS);

    TextField emailField = new TextField();
    emailField.setPromptText("Email Address");

    TextField phoneField = new TextField();
    phoneField.setPromptText("Phone Number");

    TextField streetField = new TextField();
    streetField.setPromptText("Street Address or Apartment");

    TextField cityField = new TextField();
    cityField.setPromptText("City");

    TextField stateField = new TextField();
    stateField.setPromptText("State");

    TextField zipField = new TextField();
    zipField.setPromptText("Zip Code");

    HBox cityStateZipRow = new HBox(8, cityField, stateField, zipField);
    HBox.setHgrow(cityField, Priority.ALWAYS);
    stateField.setPrefWidth(60);
    zipField.setPrefWidth(90);

    ComboBox<String> roleBox = new ComboBox<>();
    roleBox.getItems().addAll("USER", "ADMIN");
    roleBox.setValue("USER");

    // 08/13/2026 - MQ - Implement Reset Password - Add Security Question Controls
    ComboBox<String> securityQuestionBox = new ComboBox<>();
    securityQuestionBox.getItems().addAll(
        "What was the mascot of your first school?",
        "What is your favorite musical artist?",
        "What was the name of your first elementary school?",
        "What is your mother's maiden name?"
    );
    securityQuestionBox.setPromptText("Select a Security Question");
    securityQuestionBox.setMaxWidth(Double.MAX_VALUE); // Expand to fit layout

    TextField securityAnswerField = new TextField();
    securityAnswerField.setPromptText("Security Question Answer");

    // Set all registration elements to hidden initially
    // 08/13/2026 - MQ - Implement Reset Password - Include Security Question Fields
    Region[] regOnlyFields = { nameRow, emailField, phoneField, streetField, cityStateZipRow, roleBox, securityQuestionBox, securityAnswerField };
    for (Region field : regOnlyFields) {
      field.setManaged(false);
      field.setVisible(false);
    }

    Label statusLabel = new Label();
    statusLabel.setStyle("-fx-text-fill: red;");

    Button submitButton = new Button("Login");

    // 08/13/2026 - MQ - Assign explicit IDs to controls
    submitButton.setId("loginButton");

    Hyperlink toggleModeLink = new Hyperlink("Need an account? Register here");
    // Set text color using inline style
    toggleModeLink.setStyle("-fx-text-fill: #146a9b;");

    // 08/13/2026 - MQ - Implement Reset Password
    Hyperlink forgotPasswordLink = new Hyperlink("Forgot password?");
    forgotPasswordLink.setStyle("-fx-text-fill: #146a9b;");
    forgotPasswordLink.setOnAction(e ->
        stage.setScene(create(SceneType.RESET_PASSWORD, stage, db))
    );

    // 08/13/2026 - MQ - Implement Reset Password - Flag to track whether logging in or registering
    final boolean[] isRegisterMode = { false };

    toggleModeLink.setOnAction(e -> {
      isRegisterMode[0] = !isRegisterMode[0];
      boolean showReg = isRegisterMode[0];

      title.setText(showReg ? "Create New Account" : "Welcome. Please Log In");
      submitButton.setText(showReg ? "Register & Continue" : "Login");
      toggleModeLink.setText(showReg ? "Already have an account? Log in" : "Need an account? Register here");

      // 08/13/2026 - MQ - Implement Reset Password - 1. Hide Team Logo image in Registration mode
      teamLogoImageView.setVisible(!showReg);
      teamLogoImageView.setManaged(!showReg);

      // 08/13/2026 - MQ - Implement Reset Password - 2. Hide "Forgot password?" link in Registration mode
      forgotPasswordLink.setVisible(!showReg);
      forgotPasswordLink.setManaged(!showReg);

      // Toggle field visibility
      for (Region field : regOnlyFields) {
        field.setManaged(showReg);
        field.setVisible(showReg);
      }
      statusLabel.setText("");
    });

    submitButton.setOnAction(e -> {
      String user = usernameField.getText().trim();
      String pass = passwordField.getText();

      if (user.isEmpty() || pass.isEmpty()) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText("Username and Password are required.");
        return;
      }

      if (isRegisterMode[0]) {
        // registration form fields
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String phone     = phoneField.getText().trim();
        String street    = streetField.getText().trim();
        String city      = cityField.getText().trim();
        String state     = stateField.getText().trim();
        String zip       = zipField.getText().trim();
        String role      = roleBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
          statusLabel.setStyle("-fx-text-fill: red;");
          statusLabel.setText("First Name, Last Name, and Email are required.");
          return;
        }

        // 08/13/2026 - MQ - Implement Reset Password
        String question = securityQuestionBox.getValue();
        String answer = securityAnswerField.getText().trim();

        if (question == null || answer.isEmpty()) {
          statusLabel.setStyle("-fx-text-fill: red;");
          statusLabel.setText("Security question and answer are required.");
          return;
        }

        // 08/13/2026 - MQ - Implement Reset Password - Pass question & answer to database registration
        // save boolean success value
        boolean success = db.getUserDAO().registerUser(
            user, pass, email, firstName, lastName,
            phone, street, city, state, zip, role, question, answer
        );

        if (success) {
          // Set currentUser session after registration
          currentUser = user;
          stage.setScene(create(SceneType.MAIN, stage, db));
        } else {
          statusLabel.setStyle("-fx-text-fill: red;");
          statusLabel.setText("Registration failed. Username/Email may exist.");
        }
      } else {
        // if logging in, and username and password are good, authenticate the user
        if (db.getUserDAO().authenticateUser(user, pass)) {
          // Set the currentUser session after login
          currentUser = user;
          stage.setScene(create(SceneType.MAIN, stage, db));
        } else {
          statusLabel.setStyle("-fx-text-fill: red;");
          statusLabel.setText("Invalid username or password.");
        }
      }
    });

    VBox layout = new VBox(10,
        // add team logo image at the top of the login scene
        teamLogoImageView,
        title,
        usernameField,
        passwordField,
        nameRow,
        emailField,
        phoneField,
        streetField,
        cityStateZipRow,
        // 08/13/2026 - MQ - Implement Reset Password - Add Security Question Controls
        securityQuestionBox,
        securityAnswerField,
        roleBox,
        submitButton,
        // 08/13/2026 - MQ - Implement Reset Password - Add forgot password link
        forgotPasswordLink,
        toggleModeLink,
        statusLabel
    );
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(24));
    layout.setMaxWidth(380);

    StackPane root = new StackPane(layout);
    return new Scene(root, 600, 520);
  }

  /**
   * Builds the DASHBOARD scene.
   *
   * @param stage the Stage object for the application window
   * @param db  the DatabaseManager instance
   * @return a new Scene containing the dashboard UI
   */
  private static Scene buildDashboardScene(Stage stage, DatabaseManager db) {
    Label title = new Label("Management Todo List");
    title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    ListView<String> listView = new ListView<>();
    listView.getItems().addAll(db.getItemDAO().getAllItems());

    TextField inputField = new TextField();
    inputField.setPromptText("New todo...");
    HBox.setHgrow(inputField, Priority.ALWAYS);

    Button addButton = new Button("Add");
    addButton.setOnAction(e -> {
      String text = inputField.getText().trim();
      if (!text.isEmpty()) {
        db.getItemDAO().insertItem(text);
        listView.getItems().setAll(db.getItemDAO().getAllItems());
        inputField.clear();
      }
    });

    // Add delete button to remove selected item from to do list and database
    Button deleteButton = new Button("Delete");
    deleteButton.setOnAction(e -> {
      String selectedItem = listView.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        db.getItemDAO().deleteItem(selectedItem);
        listView.getItems().setAll(db.getItemDAO().getAllItems());
      }
    });

    // Add deleteButton next to addButton in inputRow
    HBox inputRow = new HBox(8, inputField, addButton, deleteButton);
    inputRow.setAlignment(Pos.CENTER);

    Button backButton = new Button("Back");
    backButton.setOnAction(e ->
        stage.setScene(create(SceneType.MAIN, stage, db))
    );

    HBox navRow = new HBox(8, backButton);
    navRow.setAlignment(Pos.CENTER_LEFT);

    VBox layout = new VBox(12, title, listView, inputRow, navRow);
    layout.setPadding(new Insets(16));
    VBox.setVgrow(listView, Priority.ALWAYS);

    // change dashboard scene height to 450
    return new Scene(layout, 600, 450);
  }


  /**
   * Builds the CART scene using Cart.fxml.
   */
  private static Scene buildCartScene(Stage stage, DatabaseManager db) {
    try {
      FXMLLoader loader = new FXMLLoader(SceneFactory.class.getResource("/Cart.fxml"));
      Parent root = loader.load();
      CartController controller = loader.getController();
      controller.setApplicationData(stage, db, currentUser);
      return new Scene(root, 600, 450);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load Cart.fxml.", e);
    }
  }
  /**
   * Builds the CHECKOUT scene using Checkout.fxml.
   */
  private static Scene buildCheckoutScene(
      Stage stage,
      DatabaseManager db
  ) {
    try {
      if (currentUser == null) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Required");
        alert.setHeaderText(
            "You must be logged in to check out."
        );

        alert.showAndWait();

        return buildLoginScene(stage, db);
      }

      int userId =
          db.getUserIdByUsername(currentUser);

      CartDao cartDao =
          new CartDao(db);

      Cart activeCart =
          cartDao.getOrCreateActiveCart(userId);

      FXMLLoader loader =
          new FXMLLoader(
              SceneFactory.class.getResource(
                  "/Checkout.fxml"
              )
          );

      Parent root = loader.load();

      CheckoutController controller =
          loader.getController();

      controller.setApplicationData(
          stage,
          db,
          activeCart.getCartId()
      );

      return new Scene(root, 600, 450);

    } catch (IOException e) {

      LOGGER.log(
          Level.SEVERE,
          "Unable to load Checkout.fxml",
          e
      );

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Checkout Error");
      alert.setHeaderText(
          "Checkout could not be opened."
      );
      alert.setContentText(
          "You will be returned to your shopping cart."
      );

      alert.showAndWait();

      return buildCartScene(stage, db);
    }  catch (SQLException e) {

      LOGGER.log(
          Level.SEVERE,
          "Database error while preparing checkout",
          e
      );

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Checkout Error");
      alert.setHeaderText(
          "Checkout could not be prepared."
      );
      alert.setContentText(
          "You will be returned to your shopping cart."
      );

      alert.showAndWait();

      return buildCartScene(stage, db);
    }
  }


  /**
   * Builds the ADD_CATEGORY scene using add-category.fxml.
   */
  private static Scene buildAddCategoryScene(Stage stage, DatabaseManager db) {
    try {
      FXMLLoader loader = new FXMLLoader(SceneFactory.class.getResource("/add-category.fxml"));
      Parent root = loader.load();

      AddCategoryController controller = loader.getController();
      CategoryDAO categoryDAO = new CategoryDAO(db);
      controller.setCategoryDAO(categoryDAO);
      controller.setNavigation(stage, db);

      return new Scene(root, 600, 450);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Could not load Add Category scene.", e);
    }
  }

  /**
   * Builds the BROWSE_PRODUCT scene using browse-product.fxml.
   */
  private static Scene buildBrowseProduct(Stage stage, DatabaseManager db) {
    try {
      FXMLLoader loader = new FXMLLoader(SceneFactory.class.getResource("/browse-product.fxml"));
      Parent root = loader.load();

      BrowseProductController controller = loader.getController();
      controller.setApplicationData(stage, db);

      return new Scene(root, 600, 450);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load browse-product.fxml.", e);
    }
  }

  /**
   * Builds the Catalog Management scene using CatalogManagement.fxml.
   *
   */
  private static Scene buildCatalogManagementScene(
      Stage stage,
      DatabaseManager db) {

    try {
      FXMLLoader loader = new FXMLLoader(
          SceneFactory.class.getResource(
              "/catalog-management.fxml"
          )
      );

      Parent root = loader.load();

      CatalogManagementController controller =
          loader.getController();

      controller.setApplicationData(stage, db);

      return new Scene(root, 600, 450);

    } catch (IOException e) {
      throw new IllegalStateException(
          "Unable to load catalog-management.fxml.",
          e
      );
    }
  }

  /**
   * Builds the Add Product scene using add-product.fxml.
   */
  private static Scene buildAddProductScene(
      Stage stage,
      DatabaseManager db) {

    try {
      FXMLLoader loader = new FXMLLoader(
          SceneFactory.class.getResource(
              "/add-product.fxml"
          )
      );

      Parent root = loader.load();

      AddProductController controller =
          loader.getController();

      controller.setApplicationData(stage, db);

      return new Scene(root, 600, 600);

    } catch (IOException e) {
      throw new IllegalStateException(
          "Unable to load add-product.fxml.",
          e
      );
    }
  }

  /**
   * Builds the Product Detail scene using product-detail.fxml.
   */
  private static Scene buildProductDetailScene(
          Stage stage,
          DatabaseManager db,
          Product product) {

    try {
      FXMLLoader loader = new FXMLLoader(
              SceneFactory.class.getResource(
                      "/product-detail.fxml"
              )
      );

      Parent root = loader.load();

      ProductDetailController controller =
              loader.getController();

      controller.setApplicationData(
              stage,
              db,
              product,
              currentUser
      );

      return new Scene(root);

    } catch (IOException e) {
      throw new RuntimeException(
              "Unable to load product-detail.fxml",
              e
      );
    }
  }

  // 08/13/2026 - MQ - Implement Reset Password
  /**
   * Builds the RESET_PASSWORD scene. Loads the fxml password layout scene.
   */
  private static Scene buildResetPasswordScene(Stage stage, DatabaseManager db) {
    try {
      // load the fxml scene
      FXMLLoader loadFxmlScene = new FXMLLoader(SceneFactory.class.getResource("/ResetPassword.fxml"));
      Parent root = loadFxmlScene.load();

      ResetPasswordController controller = loadFxmlScene.getController();
      controller.setApplicationData(stage, db);

      return new Scene(root, 600, 520);
    } catch (IOException e) {
      // cate error and display a message
      LOGGER.log(Level.SEVERE, "Sorry. Unable to load ResetPassword.fxml", e);
      throw new IllegalStateException("Sorry. Illegal State Exception. Unable to load ResetPassword.fxml.", e);
    }
  }

  //endregion
}
