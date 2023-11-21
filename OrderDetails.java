import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class OrderDetails {
    private CustomerDetails customer;
    private List<Store.Product> products;
    private double totalAmount;

    public OrderDetails(CustomerDetails customer, List<Store.Product> products, double totalAmount) {
        this.customer = customer;
        this.products = products;
        this.totalAmount = totalAmount;
    }

    public void saveToDatabase() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Insert order information into the orders table
            String orderQuery = "INSERT INTO orders (customer_email, total_amount, order_date) VALUES (?, ?, ?)";
            try (PreparedStatement orderStatement = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                orderStatement.setString(1, customer.getEmail());
                orderStatement.setDouble(2, totalAmount);
                orderStatement.setDate(3, Date.valueOf(LocalDate.now()));
                orderStatement.executeUpdate();

                // Get the auto-generated order ID
                try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        // Insert product information into the order_items table
                        String orderItemsQuery = "INSERT INTO order_items (order_id, product_name, product_price) VALUES (?, ?, ?)";
                        try (PreparedStatement orderItemsStatement = connection.prepareStatement(orderItemsQuery)) {
                            for (Store.Product product : products) {
                                orderItemsStatement.setInt(1, orderId);
                                orderItemsStatement.setString(2, product.getName());
                                orderItemsStatement.setDouble(3, product.getPrice());
                                orderItemsStatement.addBatch();
                            }
                            orderItemsStatement.executeBatch();
                        }
                    } else {
                        throw new SQLException("Failed to retrieve auto-generated keys for order ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "customer=" + customer +
                ", products=" + products +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
