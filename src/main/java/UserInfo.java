/**
 * Data model that represents a user account for admin management views.
 *
 * @author Miguel Quezada
 * @version 0.1.0
 * @since 2026-08-02
 */
public class UserInfo {
  private final int id;
  private final String username;
  private final String email;
  private final String role;
  private final String createdAt;

  public UserInfo(int id, String username, String email, String role, String createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.role = role;
    this.createdAt = createdAt;
  }

  public int getId() { return id; }
  public String getUsername() { return username; }
  public String getEmail() { return email; }
  public String getRole() { return role; }
  public String getCreatedAt() { return createdAt; }
}