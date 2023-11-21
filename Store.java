import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Store {
    private List<Product> inventory = new ArrayList<>();
    private CustomerDetails currentCustomer;
    private List<CustomerDetails> customers = new ArrayList<>();
    private List<OrderDetails> orderHistory = new ArrayList<>();

    public Store() {
        // Hardcode products into the inventory
        inventory.add(new Bike("Kid's Bike: Mountain Bike", BikeCategory.MOUNTAIN_BIKE, 149.99));
        inventory.add(new Bike("Kid's Bike: Road Bike", BikeCategory.ROAD_BIKE, 129.99));
        inventory.add(new Bike("Kid's Bike: BMX Bike", BikeCategory.BMX_BIKE, 89.99));
        inventory.add(new Bike("Kid's Bike: Cruiser Bike", BikeCategory.CRUISER_BIKE, 109.99));
    }

    public void welcome() {
        System.out.println("Welcome to our Kids Bike Store!");
        System.out.println("Login -> 1");
        System.out.println("Sign Up -> 2");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 1) {
            login();
        } else if (choice == 2) {
            signUp();
        } else {
            System.out.println("Invalid choice. Exiting.");
            System.exit(0);
        }
    }

    private void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        currentCustomer = findCustomerByEmail(email);

        if (currentCustomer != null) {
            System.out.println("Welcome back, " + currentCustomer.getName() + "!");
        } else {
            System.out.println("No account found with the given email. Signing up a new account.");
            signUp();
        }
    }

    private CustomerDetails findCustomerByEmail(String email) {
        for (CustomerDetails customer : customers) {
            if (customer.getEmail().equals(email)) {
                return customer;
            }
        }
        return null;
    }

    public void signUp() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        currentCustomer = new CustomerDetails(name, email);
        customers.add(currentCustomer);
        System.out.println("Sign-up successful! Welcome, " + name + "!");
    }

    public void displayInventory() {
        System.out.println("Available Products:");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i + 1) + ". " + inventory.get(i).getName());
        }
    }

    public void shop() {
        ShoppingCart cart = new ShoppingCart();

        Scanner scanner = new Scanner(System.in);
        boolean continueShopping = true;

        while (continueShopping) {
            displayInventory();
            System.out.print("Enter a number choice for what you want to do (5 to Finish Shopping, 6 to Continue Shopping, 7 to Return a Bike): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 5: // To stop shopping
                    continueShopping = false;
                    break;
                case 6: // To continue shopping
                    break;
                case 7: // To return a bike
                    cart.displayItems();
                    System.out.print("Enter the number of the operation you want to perform:\n1. Remove Product\n2. Return Product\n0. Continue Shopping\nChoice: ");
                    int operationChoice = scanner.nextInt();
                    if (operationChoice == 1) {
                        System.out.print("Enter the index of the product you want to remove: ");
                        int removeIndex = scanner.nextInt();
                        cart.removeProduct(removeIndex - 1); // Subtracting 1 to convert to zero-based index
                    } else if (operationChoice == 2) {
                        cart.returnProduct();
                    }
                    break;
                default:
                    if (choice >= 1 && choice <= inventory.size()) {
                        Product selectedProduct = inventory.get(choice - 1);
                        cart.addProduct(selectedProduct);
                        System.out.println("You've added " + selectedProduct.getName() + " to your cart.");
                    } else {
                        System.out.println("Invalid input. Please select a valid option or product number.");
                    }
                    break;
            }
        }

        double totalPrice = cart.calculateTotalPrice();
        System.out.println("Thank you for shopping with us!");
        System.out.println("Total Price: $" + totalPrice);

        applyLoyaltyPointsDiscount();

        PaymentProcessor paymentProcessor = new PaymentProcessor();
        paymentProcessor.processPayment(totalPrice, currentCustomer);

        // Save order details to order history
        OrderDetails order = new OrderDetails(currentCustomer, cart.getItems(), totalPrice, LocalDate.now());
        orderHistory.add(order);
    }

    private void applyLoyaltyPointsDiscount() {
        if (currentCustomer.getLoyaltyPoints() > 0) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Do you want to use your loyalty points for a discount? (yes/no): ");
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("yes")) {
                int loyaltyPointsToUse = currentCustomer.getLoyaltyPoints();
                double discountAmount = loyaltyPointsToUse * 0.01; // Assuming 1 loyalty point = $0.01 discount
                currentCustomer.setLoyaltyPoints(0); // Reset loyalty points after using them
                System.out.println("Loyalty points discount applied: $" + discountAmount);
            }
        }
    }

    public void viewOrderHistory() {
        System.out.println("Order History:");
        for (OrderDetails order : orderHistory) {
            System.out.println(order);
        }
    }

    public static void main(String[] args) {
        Store store = new Store();
        store.welcome();
        store.shop();
        store.viewOrderHistory();
    }

    static class Product {
        private String name;
        private double price;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    static class Bike extends Product {
        private BikeCategory category;

        public Bike(String name, BikeCategory category, double price) {
            super(name, price);
            this.category = category;
        }

        // Additional methods specific to bikes...
    }

    static class ShoppingCart {
        private List<Product> items = new ArrayList<>();
        private LocalDate purchaseDate;

        public void addProduct(Product product) {
            items.add(product);
            purchaseDate = LocalDate.now(); // Record the purchase date when a product is added
        }

        public void removeProduct(int index) {
            if (index >= 0 && index < items.size()) {
                items.remove(index);
                System.out.println("Product removed from the cart.");
            } else {
                System.out.println("Invalid index. Product not removed.");
            }
        }

        public void displayItems() {
            System.out.println("Items in the cart:");
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". " + items.get(i).getName());
            }
        }

        public void returnProduct() {
            displayItems();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the number of the product you want to return: ");
            int returnChoice = scanner.nextInt();

            if (returnChoice >= 1 && returnChoice <= items.size()) {
                Product returnedProduct = items.get(returnChoice - 1);
                LocalDate currentDate = LocalDate.now();

                // Check if the return date is within 7 days of the purchase date
                if (currentDate.minusDays(7).isAfter(purchaseDate)) {
                    System.out.println("Sorry, you can't return the product. Return period has expired.");
                } else {
                    items.remove(returnChoice - 1);
                    System.out.println("You've returned " + returnedProduct.getName() + ".");
                }
            } else {
                System.out.println("Invalid input. Product not returned.");
            }
        }

        public List<Product> getItems() {
            return items;
        }

        public double calculateTotalPrice() {
            return items.stream().mapToDouble(Product::getPrice).sum();
        }
    }

    static class PaymentProcessor {
        public void processPayment(double totalAmount, CustomerDetails customer) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the amount of cash you're paying with: $");
            double payment = scanner.nextDouble();

            double change = payment - totalAmount;

            if (change >= 0) {
                System.out.println("Change: $" + change);
            } else {
                System.out.println("Insufficient payment. Please pay the full amount.");
            }
        }
    }

    static class CustomerDetails {
        private String name;
        private String email;
        private int loyaltyPoints;

        public CustomerDetails(String name, String email) {
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

        @Override //added a \n to space things out
        public String toString() {
            return "CustomerDetails{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", loyaltyPoints=" + loyaltyPoints +
                    '}';
        }
    }

    static class OrderDetails {
        private CustomerDetails customer;
        private List<Product> products;
        private double totalAmount;
        private LocalDate orderDate;

        public OrderDetails(CustomerDetails customer, List<Product> products, double totalAmount, LocalDate orderDate) {
            this.customer = customer;
            this.products = products;
            this.totalAmount = totalAmount;
            this.orderDate = orderDate;
        }

        @Override
        public String toString() {
            return "OrderDetails{" +
                    "customer=" + customer +
                    ", products=" + products +
                    ", totalAmount=" + totalAmount +
                    ", orderDate=" + orderDate +
                    '}';
        }
    }
}
