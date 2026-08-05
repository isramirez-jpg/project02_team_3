/**
 * Name: Ha Nguyen
 * Date: 8/4/2026
 * Explanation: This class represents a product in the clothing catalog.
 * It stores the data for one product from the products database table.
 * A product belongs to a category through the categoryId field.
 */
public class Product {
    private int productId;
    private int categoryId;
    private String productName;
    private String description;
    private double price;
    private String gender;
    private String color;
    private String size;
    private int stockQuantity;
    private String imagePath;

    public Product(int productId, int categoryId, String productName, String description, double price, String gender, String color, String size, int stockQuantity, String imagePath) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.gender = gender;
        this.color = color;
        this.size = size;
        this.stockQuantity = stockQuantity;
        this.imagePath = imagePath;
    }

    public Product(int categoryId, String productName, String description, double price, String gender, String color, String size, int stockQuantity, String imagePath) {
        this.categoryId = categoryId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.gender = gender;
        this.color = color;
        this.size = size;
        this.stockQuantity = stockQuantity;
        this.imagePath = imagePath;
    }

    public Product(int productId, int categoryId, String productName, double price, String size, int stockQuantity) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    public Product(int categoryId, String productName, double price, String size, int stockQuantity) {
        this.categoryId = categoryId;
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", categoryId=" + categoryId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", gender='" + gender + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
