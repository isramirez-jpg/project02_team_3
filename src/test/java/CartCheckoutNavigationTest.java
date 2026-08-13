import javafx.scene.Scene;
import javafx.stage.Stage;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;


/**
 * @author Isabel Ramirez
 * @created 8.13.26
 * Description: An LLM was used to assist with drafting test cases
 */
public class CartCheckoutNavigationTest
        extends ApplicationTest {

    private Connection connection;
    private DatabaseManager db;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        connection =
                CartCheckoutTestDatabase.createConnection();

        db = new DatabaseManager(
                connection,
                false
        );

        // Add a test user
        try (PreparedStatement statement =
                     connection.prepareStatement("""
                         INSERT INTO users
                             (username, password_hash, email)
                         VALUES (?, ?, ?)
                         """)) {

            statement.setString(1, "testfx_user");
            statement.setString(
                    2, BCrypt.hashpw("testPassword", BCrypt.gensalt()
                    )
            );
            statement.setString(
                    3,
                    "testfx@example.com"
            );

            statement.executeUpdate();
        }

        int userId =
                db.getUserIdByUsername(
                        "testfx_user"
                );

        CartDao cartDao =
                new CartDao(db);

        Cart cart =
                cartDao.create(userId);

        int productId =
                insertTestProduct();

        cartDao.addProduct(
                cart.getCartId(),
                productId,
                1
        );

        setCurrentUserForTest("testfx_user");

        Scene scene =
                SceneFactory.create(
                        SceneType.CART,
                        stage,
                        db
                );

        stage.setScene(scene);
        stage.show();
    }
    private void setCurrentUserForTest(String username)
            throws Exception {

        Field currentUserField =
                SceneFactory.class.getDeclaredField(
                        "currentUser"
                );

        currentUserField.setAccessible(true);
        currentUserField.set(null, username);
    }

    private int insertTestProduct()
            throws Exception {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             INSERT INTO products
                                 (product_name,
                                  price,
                                  stock_quantity)
                             VALUES (?, ?, ?)
                             """,
                             java.sql.Statement
                                     .RETURN_GENERATED_KEYS
                     )) {

            statement.setString(
                    1,
                    "TestFX Shirt"
            );

            statement.setBigDecimal(
                    2,
                    new java.math.BigDecimal("19.99")
            );

            statement.setInt(3, 10);

            statement.executeUpdate();

            try (var keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new IllegalStateException(
                "Test product could not be created."
        );
    }
    @Test
    void nonemptyCartCanNavigateToCheckout() {

        javafx.scene.control.Button button =
                lookup("#checkoutButton").queryButton();

        interact(button::fire);

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(
                "#checkoutTitle",
                isVisible()
        );
    }

    @Override
    public void stop() throws Exception {

        if (connection != null) {
            connection.close();
        }
    }
}