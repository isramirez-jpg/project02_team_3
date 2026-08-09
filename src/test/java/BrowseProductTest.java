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
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        BrowseProductTest.class.getResource(
                                "/browse-product.fxml"
                        )
                );

                Parent root = loader.load();

                assertNotNull(root);

                BrowseProductController controller =
                        loader.getController();

                assertNotNull(controller);

                ListView<Product> productListView =
                        (ListView<Product>) root.lookup("#productListView");

                assertNotNull(productListView);

            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}
