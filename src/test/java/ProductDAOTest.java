import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Name: Ha Nguyen
 * Date: 8/13/2026
 * Tests the ProductDAO database operations.
 *
 * LLM Assistance:
 * ChatGPT was used to assist with writing unit tests for adding
 * and removing products and with verifying the test assertions.
 *
 * Prompt used:
 * "Write unit tests for adding and removing products using the
 * existing ProductDAO and Product classes. Add tests for inserting
 * and deleting a product and verify that the product is correctly
 * stored in and removed from the database."
 */
public class ProductDAOTest {

    @Test
    void testInsertAndReadProduct() {

        // Create database manager and DAOs
        DatabaseManager db = new DatabaseManager();
        CategoryDAO categoryDAO = new CategoryDAO(db);
        ProductDAO productDAO = new ProductDAO(db);

        // Create a category for the product
        Category category = new Category(
                "Test Product Category",
                "Test category for product"
        );

        Category savedCategory = categoryDAO.insert(category);

        assertNotNull(savedCategory);
        assertTrue(savedCategory.getCategoryId() > 0);

        // Create a new product
        Product product = new Product(
                0,
                savedCategory.getCategoryId(),
                "Test T-Shirt",
                "Test product for unit testing",
                19.99,
                "Unisex",
                "Black",
                "M",
                10,
                "test-tshirt.jpg"
        );

        // Insert the product
        Product savedProduct = productDAO.insert(product);

        // Verify the product was inserted
        assertNotNull(savedProduct);
        assertTrue(savedProduct.getProductId() > 0);

        // Read the product from the database
        Product foundProduct =
                productDAO.getById(savedProduct.getProductId());

        // Verify the product information
        assertNotNull(foundProduct);
        assertEquals(
                "Test T-Shirt",
                foundProduct.getProductName()
        );
        assertEquals(
                savedCategory.getCategoryId(),
                foundProduct.getCategoryId()
        );
        assertEquals(
                "Test product for unit testing",
                foundProduct.getDescription()
        );
        assertEquals(
                19.99,
                foundProduct.getPrice()
        );
        assertEquals(
                "Unisex",
                foundProduct.getGender()
        );
        assertEquals(
                "Black",
                foundProduct.getColor()
        );
        assertEquals(
                "M",
                foundProduct.getSize()
        );
        assertEquals(
                10,
                foundProduct.getStockQuantity()
        );
        assertEquals(
                "test-tshirt.jpg",
                foundProduct.getImagePath()
        );

        // Close the database connection
        db.close();
    }

    @Test
    void testDeleteProduct() {

        // Create database manager and DAOs
        DatabaseManager db = new DatabaseManager();
        CategoryDAO categoryDAO = new CategoryDAO(db);
        ProductDAO productDAO = new ProductDAO(db);

        // Create a category for the product
        Category category = new Category(
                "Test Delete Category",
                "Category for product delete test"
        );

        Category savedCategory = categoryDAO.insert(category);

        assertNotNull(savedCategory);
        assertTrue(savedCategory.getCategoryId() > 0);

        // Create a product to delete
        Product product = new Product(
                0,
                savedCategory.getCategoryId(),
                "Test Delete Product",
                "Product for delete test",
                29.99,
                "Women",
                "Blue",
                "L",
                5,
                "delete-test.jpg"
        );

        // Insert the product first
        Product savedProduct = productDAO.insert(product);

        assertNotNull(savedProduct);
        assertTrue(savedProduct.getProductId() > 0);

        // Delete the product
        boolean deleted =
                productDAO.delete(savedProduct.getProductId());

        // Verify the product was deleted
        assertTrue(deleted);

        // Verify the product can no longer be found
        Product deletedProduct =
                productDAO.getById(savedProduct.getProductId());

        assertNull(deletedProduct);

        // Close the database connection
        db.close();
    }
}