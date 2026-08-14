import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Name: Ha Nguyen
 * Date: 8/8/2026
 *
 * Explanation:
 * This class tests the Browse Product functionality of the clothing
 * catalog application. It verifies that the browse-product FXML loads
 * correctly and that products can be searched by product name.
 *
 * LLM Assistance:
 * ChatGPT was used to assist with creating unit tests for browsing
 * and searching products based on the existing BrowseProductController.
 *
 * Prompt used:
 * "Based on my BrowseProductController.java, which includes a
 * searchProducts(List<Product>, String) method, write JUnit 5
 * unit tests for browsing and searching products. Include tests
 * for a matching search, no matching results, and an empty search.
 */
public class BrowseProductTest {

    /**
     * Initializes JavaFX before the tests run.
     */
    @BeforeAll
    static void initializeJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.startup(latch::countDown);

        latch.await();
    }

    /**
     * Tests that the browse-product FXML loads correctly and that
     * the product TableView is connected to the FXML.
     */
    @Test
    void testBrowseProductFXMLLoads() throws Exception {

        FXMLLoader loader = new FXMLLoader(
                BrowseProductTest.class.getResource(
                        "/browse-product.fxml"
                )
        );

        Parent root = loader.load();

        assertNotNull(
                root,
                "FXML root was not created"
        );

        BrowseProductController controller =
                loader.getController();

        assertNotNull(
                controller,
                "BrowseProductController was not instantiated"
        );

        TableView<?> productTableView =
                (TableView<?>) root.lookup("#productTableView");

        assertNotNull(
                productTableView,
                "productTableView was not found in FXML"
        );
    }

    /**
     * Tests that searching for an existing product name
     * returns the matching product.
     */
    @Test
    void testSearchProductsWithMatchingResult() {

        BrowseProductController controller =
                new BrowseProductController();

        Product shirt = new Product(
                1,
                1,
                "Blue Shirt",
                "Blue cotton shirt",
                25.00,
                "Men",
                "Blue",
                "M",
                10,
                null
        );

        Product pants = new Product(
                2,
                2,
                "Black Pants",
                "Black casual pants",
                35.00,
                "Women",
                "Black",
                "L",
                8,
                null
        );

        List<Product> products =
                Arrays.asList(shirt, pants);

        List<Product> results =
                controller.searchProducts(products, "shirt");

        assertEquals(1, results.size());
        assertEquals("Blue Shirt",
                results.get(0).getProductName());
    }

    /**
     * Tests that searching for a product that does not exist
     * returns an empty list.
     */
    @Test
    void testSearchProductsWithNoMatchingResults() {

        BrowseProductController controller =
                new BrowseProductController();

        Product shirt = new Product(
                1,
                1,
                "Blue Shirt",
                "Blue cotton shirt",
                25.00,
                "Men",
                "Blue",
                "M",
                10,
                null
        );

        Product pants = new Product(
                2,
                2,
                "Black Pants",
                "Black casual pants",
                35.00,
                "Women",
                "Black",
                "L",
                8,
                null
        );

        List<Product> products =
                Arrays.asList(shirt, pants);

        List<Product> results =
                controller.searchProducts(products, "dress");

        assertTrue(
                results.isEmpty(),
                "Search with no matching products should return an empty list"
        );
    }

    /**
     * Tests that an empty search returns all available products.
     */
    @Test
    void testSearchProductsWithEmptySearch() {

        BrowseProductController controller =
                new BrowseProductController();

        Product shirt = new Product(
                1,
                1,
                "Blue Shirt",
                "Blue cotton shirt",
                25.00,
                "Men",
                "Blue",
                "M",
                10,
                null
        );

        Product pants = new Product(
                2,
                2,
                "Black Pants",
                "Black casual pants",
                35.00,
                "Women",
                "Black",
                "L",
                8,
                null
        );

        List<Product> products =
                Arrays.asList(shirt, pants);

        List<Product> results =
                controller.searchProducts(products, "");

        assertEquals(
                products.size(),
                results.size(),
                "Empty search should return all products"
        );
    }
}