import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Name: Ha Nguyen
 * Date: 8/4/2026
 * Tests the CategoryDAO database operations.
 *
 * LLM Assistance:
 * ChatGPT was used to assist with writing unit tests for adding
 * and removing categories and with verifying the test assertions.
 *
 * Prompt used:
 * "Write unit tests for adding and removing categories
 * using the existing CategoryDAO classes."
 */
public class CategoryDAOTest {

    @Test
    void testInsertAndReadCategory() {

        // Create database manager and DAO
        DatabaseManager db = new DatabaseManager();
        CategoryDAO categoryDAO = new CategoryDAO(db);

        // Create a new category
        Category category = new Category(
                "Test Dresses",
                "Test category for clothing"
        );

        // Insert the category
        Category savedCategory = categoryDAO.insert(category);

        // Verify the category was inserted
        assertNotNull(savedCategory);
        assertTrue(savedCategory.getCategoryId() > 0);

        // Read the category from the database
        Category foundCategory =
                categoryDAO.getById(savedCategory.getCategoryId());

        // Verify the category information
        assertNotNull(foundCategory);
        assertEquals(
                "Test Dresses",
                foundCategory.getCategoryName()
        );
        assertEquals(
                "Test category for clothing",
                foundCategory.getDescription()
        );

        // Close the database connection
        db.close();
    }

    @Test
    void testDeleteCategory() {

        // Create database manager and DAO
        DatabaseManager db = new DatabaseManager();
        CategoryDAO categoryDAO = new CategoryDAO(db);

        // Create a category to delete
        Category category = new Category(
                "Test Delete Category",
                "Category for delete test"
        );

        // Insert the category first
        Category savedCategory = categoryDAO.insert(category);

        assertNotNull(savedCategory);
        assertTrue(savedCategory.getCategoryId() > 0);

        // Delete the category
        boolean deleted =
                categoryDAO.delete(savedCategory.getCategoryId());

        // Verify the category was deleted
        assertTrue(deleted);

        // Verify the category can no longer be found
        Category deletedCategory =
                categoryDAO.getById(savedCategory.getCategoryId());

        assertNull(deletedCategory);

        // Close the database connection
        db.close();
    }
}