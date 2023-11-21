import java.sql.*;

public class CustomerDetails {
    private String name;
    private String email;
    private int loyaltyPoints;

    public CustomerDetails(String name, String email, int loyaltyPoints) {
        this.name = name;
        this.email = email;
        this.loyaltyPoints = 0;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void saveToDatabase() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "INSERT INTO customers (name, email, loyaltyPoints) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setInt(3, loyaltyPoints);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CustomerDetails loadFromDatabase(String email) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM customers WHERE email=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int loyaltyPoints = resultSet.getInt("loyaltyPoints");
                        return new CustomerDetails(name, email, loyaltyPoints);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomerDetails{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                '}';
    }
}
