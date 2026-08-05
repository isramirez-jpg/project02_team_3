import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Name: Ha Nguyen
 * Date: 8/4/2026
 * Tests the CategoryDAO database operations.
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
        assertNotNull(savedCategory.getCategoryId());

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
}
