import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Name: Ha Nguyen
 * Date: 8/8/2026
 *
 * LLM assistance: use ChatGPT (OpenAI)
 * Prompt used:
 *
 * Based on provided BrowseProductController.java class and browse-product.fxml
 * file for a JavaFX clothing catalog application. Please help me create
 * BrowseProductTest.java using JUnit 5. Test that the FXML file loads
 * correctly, the BrowseProductController is created, and the product
 * ListView is properly connected to the FXML file.
 *
 * Explanation:
 * This class tests the browse-product user interface and its connection
 * to the BrowseProductController. It verifies that the FXML file loads
 * correctly, the controller is created, and the product ListView is
 * properly connected to the FXML file.
 */

public class BrowseProductTest {

    @BeforeAll
    static void initializeJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.startup(latch::countDown);

        latch.await();
    }

    @Test
    void testBrowseProductFXMLLoads() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                BrowseProductTest.class.getResource(
                        "/browse-product.fxml"
                )
        );

        Parent root = loader.load();

        assertNotNull(root, "FXML root was not created");

        BrowseProductController controller =
                loader.getController();

        assertNotNull(
                controller,
                "BrowseProductController was not instantiated"
        );

        ListView productListView =
                (ListView) root.lookup("#productListView");

        assertNotNull(
                productListView,
                "productListView was not found in FXML"
        );

        // Verify that FXMLLoader injected the ListView
        // into the controller's private field.
        var field = BrowseProductController.class
                .getDeclaredField("productListView");

        field.setAccessible(true);

        Object injectedListView = field.get(controller);

        assertSame(
                productListView,
                injectedListView,
                "productListView was not injected into the controller"
        );
    }
}
