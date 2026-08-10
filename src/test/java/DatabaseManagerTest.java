import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManagerTest {

  private DatabaseManager databaseManager;
  private Connection jdbcConnection;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create a SQLite jdbc connection
    jdbcConnection = DriverManager.getConnection("jdbc:sqlite::memory:");

    // Initialize the DatabaseManager and pass in the connection to the
    // DatabaseManager constructor method
    databaseManager = new DatabaseManager(jdbcConnection);

    // Setup a mock test user using the registerUser method which handles
    // BCrypt hashing automatically.
    databaseManager.getUserDAO().registerUser(
        "Sonicsfan94",
        "testPasswordLongLiveRockNRoll12!",
        "eddie@pearljam.com",
        "Eddie",
        "Vedder",
        "867-5309",
        "334 1st Avenue",
        "Seattle",
        "WA",
        "98109",
        "USER"
    );
  }

  /**
   * Verifies that authenticateUser() method returns true when
   * given a valid matching username and password.
   */
  @Test
  public void authenticateUser() {
    // Define the test user username and password
    String username = "Sonicsfan94";
    String inputPassword = "testPasswordLongLiveRockNRoll12!";

    // Execute the authenticateUser method using the test user
    boolean isTheUserAuthenticated =
            databaseManager.getUserDAO().authenticateUser(
                    username,
                    inputPassword
            );

    // Assert that authentication succeeds
    assertTrue(isTheUserAuthenticated, "Authentication should succeed for valid username and password.");
  }

}