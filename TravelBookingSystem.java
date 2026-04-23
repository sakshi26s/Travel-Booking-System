package travel_booking_system;

//public class main {
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
//
//}

import java.sql.*;
import java.util.Scanner;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class TravelBookingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/travel_booking_system";
    private static final String USER = "root";
    private static final String PASSWORD = "root2244";
    private Connection connection;
    private final Scanner scanner = new Scanner(System.in);
    private int currentUserId = -1;
    private String currentUserRole = "";

    public TravelBookingSystem() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== AUTHENTICATION METHODS ====================
    public void registerUser() throws SQLException {
        System.out.print("Enter First Name: ");
        String firstName = scanner.next();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.next();
        
        String email;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.next();
            if (email.matches(emailRegex)) break;
            System.out.println("Invalid email format. Please enter a valid email address.");
        }

        String phone;
        while (true) {
            System.out.print("Enter Phone Number (10 digits): ");
            phone = scanner.next();
            if (phone.matches("\\d{10}")) break;
            System.out.println("Invalid phone number. Please enter a 10-digit phone number.");
        }

        System.out.print("Enter Password: ");
        String password = scanner.next();
        
        String role;
        do {
            System.out.print("Enter Role (Customer/Admin): ");
            role = scanner.next();
            if (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("admin")) {
                System.out.println("Invalid role. Please enter 'Customer' or 'Admin'.");
            }
        } while (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("admin"));

        String query = "INSERT INTO User (first_name, last_name, email, password, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, phone);
            stmt.setString(6, role.toLowerCase()); // Make sure it's lowercase
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                
                // Insert into respective role table - FIXED THIS PART
                if (role.equalsIgnoreCase("admin")) {
                    String adminQuery = "INSERT INTO Admin (admin_id) VALUES (?)";
                    try (PreparedStatement adminStmt = connection.prepareStatement(adminQuery)) {
                        adminStmt.setInt(1, userId);
                        adminStmt.executeUpdate();
                        System.out.println("Admin user created successfully! Admin ID: " + userId);
                    }
                } else {
                    String customerQuery = "INSERT INTO Customer (customer_id) VALUES (?)";
                    try (PreparedStatement customerStmt = connection.prepareStatement(customerQuery)) {
                        customerStmt.setInt(1, userId);
                        customerStmt.executeUpdate();
                        System.out.println("Customer registered successfully! Customer ID: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
            throw e;
        }
    }
    /*
    public void registerUser() throws SQLException {
        System.out.print("Enter First Name: ");
        String firstName = scanner.next();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.next();
        
        String email;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.next();
            if (email.matches(emailRegex)) break;
            System.out.println("Invalid email format. Please enter a valid email address.");
        }

        String phone;
        while (true) {
            System.out.print("Enter Phone Number (10 digits): ");
            phone = scanner.next();
            if (phone.matches("\\d{10}")) break;
            System.out.println("Invalid phone number. Please enter a 10-digit phone number.");
        }

        System.out.print("Enter Password: ");
        String password = scanner.next();
        
        String role;
        do {
            System.out.print("Enter Role (Customer/Admin): ");
            role = scanner.next();
            if (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("admin")) {
                System.out.println("Invalid role. Please enter 'Customer' or 'Admin'.");
            }
        } while (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("admin"));

        String query = "INSERT INTO User (first_name, last_name, email, password, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, phone);
            stmt.setString(6, role.toLowerCase());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                
                // Insert into respective role table
                if (role.equalsIgnoreCase("admin")) {
                    String adminQuery = "INSERT INTO Admin (admin_id) VALUES (?)";
                    try (PreparedStatement adminStmt = connection.prepareStatement(adminQuery)) {
                        adminStmt.setInt(1, userId);
                        adminStmt.executeUpdate();
                    }
                } else {
                    String customerQuery = "INSERT INTO Customer (customer_id) VALUES (?)";
                    try (PreparedStatement customerStmt = connection.prepareStatement(customerQuery)) {
                        customerStmt.setInt(1, userId);
                        customerStmt.executeUpdate();
                    }
                }
                
                System.out.println("User registered successfully! User ID: " + userId);
            }
        }
    }
    */

    public void login() throws SQLException {
        System.out.print("Enter Email: ");
        String email = scanner.next();
        System.out.print("Enter Password: ");
        String password = scanner.next();

        String query = "SELECT user_id, role FROM User WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("user_id");
                currentUserRole = rs.getString("role");
                System.out.println("Login successful! Welcome " + currentUserRole);
                
                if (currentUserRole.equalsIgnoreCase("customer")) {
                    showCustomerMenu();
                } else {
                    showAdminMenu();
                }
            } else {
                System.out.println("Invalid email or password.");
            }
        }
    }

    // ==================== CUSTOMER METHODS ====================
    private void showCustomerMenu() throws SQLException {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("         CUSTOMER MENU           ");
            System.out.println("=================================");
            System.out.printf("%-3s%-25s%n", "1.", "View All Destinations");
            System.out.printf("%-3s%-25s%n", "2.", "View Departure Places");
            System.out.printf("%-3s%-25s%n", "3.", "Search Packages");
            System.out.printf("%-3s%-25s%n", "4.", "Book Package (Procedure)");
            System.out.printf("%-3s%-25s%n", "5.", "View My Bookings");
            System.out.printf("%-3s%-25s%n", "6.", "Add Review");
            System.out.printf("%-3s%-25s%n", "7.", "View My Reviews");
            System.out.printf("%-3s%-25s%n", "8.", "View Loyalty Points");
            System.out.printf("%-3s%-25s%n", "9.", "Check Package Availability (Function)");
            System.out.printf("%-3s%-25s%n", "0.", "Logout");
            System.out.println("=================================");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> viewAllDestinations();
                case 2 -> viewDeparturePlaces();
                case 3 -> searchPackages();
                case 4 -> bookPackageWithProcedure();
                case 5 -> viewMyBookings();
                case 6 -> addReview();
                case 7 -> viewMyReviews();
                case 8 -> viewLoyaltyPoints();
                case 9 -> checkPackageAvailability();
                case 0 -> {
                    System.out.println("Logging out...");
                    currentUserId = -1;
                    currentUserRole = "";
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ==================== PROCEDURE & FUNCTION METHODS ====================

    // Method using STORED PROCEDURE for booking
    private void bookPackageWithProcedure() throws SQLException {
        System.out.print("Enter Package ID to book: ");
        int packageId = scanner.nextInt();
        System.out.print("Enter Number of People: ");
        int numPeople = scanner.nextInt();

        // Check package availability using FUNCTION
        if (!isPackageAvailable(packageId)) {
            System.out.println("Sorry, this package is not available at the moment.");
            return;
        }

        System.out.print("Enter Payment Method (UPI/Credit Card/Debit Card/Cash): ");
        String paymentMethod = scanner.next();

        // Use stored procedure for atomic transaction
        String sql = "{call CreateBookingWithPayment(?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, packageId);
            stmt.setInt(3, numPeople);
            stmt.setString(4, paymentMethod);
            
            stmt.execute();
            System.out.println("Booking confirmed using stored procedure!");
        }
    }

    // Method using FUNCTION to check package availability
    private void checkPackageAvailability() throws SQLException {
        System.out.print("Enter Package ID to check availability: ");
        int packageId = scanner.nextInt();

        String sql = "SELECT IsPackageAvailable(?) as available";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                boolean available = rs.getBoolean("available");
                if (available) {
                    System.out.println("Package is AVAILABLE for booking!");
                } else {
                    System.out.println("Package is NOT AVAILABLE at the moment.");
                }
            }
        }
    }

    // Helper method using FUNCTION
    private boolean isPackageAvailable(int packageId) throws SQLException {
        String sql = "SELECT IsPackageAvailable(?) as available";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("available");
            }
        }
        return false;
    }

    // Method using FUNCTION to calculate discount
    private BigDecimal calculateLoyaltyDiscount(int userId) throws SQLException {
        String sql = "SELECT CalculateDiscount(?) as discount";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("discount");
            }
        }
        return BigDecimal.ZERO;
    }

    // ==================== EXISTING METHODS (Updated) ====================

    private void viewAllDestinations() throws SQLException {
        String query = "SELECT dest_id, name, country, description FROM Destination";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- All Destinations ---");
            System.out.println("ID | Name | Country | Description");
            System.out.println("----------------------------------");
            while (rs.next()) {
                System.out.printf("%-3d %-20s %-15s %-30s\n",
                    rs.getInt("dest_id"),
                    rs.getString("name"),
                    rs.getString("country"),
                    rs.getString("description") != null ? 
                        rs.getString("description").substring(0, Math.min(30, rs.getString("description").length())) : "N/A");
            }
        }
    }

    private void viewDeparturePlaces() throws SQLException {
        String query = "SELECT DISTINCT from_location FROM Package";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- Departure Places ---");
            while (rs.next()) {
                System.out.println("- " + rs.getString("from_location"));
            }
        }
    }
//l
//    private void searchPackages() throws SQLException {
//        System.out.print("Enter Source Location: ");
//        String source = scanner.next();
//        System.out.print("Enter Destination Location: ");
//        String destination = scanner.next();
//
//        // Using stored procedure for package search
//        String sql = "{call GetPackagesByDestination(?)}";
//        try (CallableStatement stmt = connection.prepareCall(sql)) {
//            stmt.setString(1, destination);
//            ResultSet rs = stmt.executeQuery();
//
//            System.out.println("\n--- Search Results ---");
//            System.out.println("PackageID | From | To | Cost | Days | Rating | Hotel | Transport");
//            System.out.println("-----------------------------------------------------------------");
//            boolean found = false;
//            while (rs.next()) {
//                found = true;
//                System.out.printf("%-10d %-10s %-10s %-8.2f %-5d %-7.1f %-15s %-10s\n",
//                    rs.getInt("package_id"),
//                    rs.getString("from_location"),
//                    rs.getString("to_location"),
//                    rs.getBigDecimal("cost"),
//                    rs.getInt("duration_days"),
//                    rs.getBigDecimal("avg_review"),
//                    rs.getString("hotel_name"),
//                    rs.getString("transport_type"));
//            }
//            if (!found) {
//                System.out.println("No packages found for your search criteria.");
//            }
//        }
//    }
//Much simpler query - just read the stored cost
private void searchPackages() throws SQLException {
 System.out.print("Enter Source Location: ");
 String source = scanner.next();
 System.out.print("Enter Destination Location: ");
 String destination = scanner.next();

 String query = "SELECT p.package_id, p.from_location, p.to_location, p.cost, p.duration_days, " +
               "p.avg_review, h.name as hotel_name, h.type as hotel_type, " +
               "t.type as transport_type " +
               "FROM Package p " +
               "JOIN Hotel h ON p.hotel_id = h.hotel_id " +
               "JOIN Transport t ON p.transport_id = t.transport_id " +
               "WHERE p.from_location LIKE ? AND p.to_location LIKE ?";
 
 try (PreparedStatement stmt = connection.prepareStatement(query)) {
     stmt.setString(1, "%" + source + "%");
     stmt.setString(2, "%" + destination + "%");
     ResultSet rs = stmt.executeQuery();

     System.out.println("\n--- Search Results ---");
     System.out.println("PackageID | From | To | Cost | Days | Rating | Hotel | Transport");
     System.out.println("-----------------------------------------------------------------");
     boolean found = false;
     while (rs.next()) {
         found = true;
         System.out.printf("%-10d %-10s %-10s $%-8.2f %-5d %-7.1f %-15s %-10s\n",
             rs.getInt("package_id"),
             rs.getString("from_location"),
             rs.getString("to_location"),
             rs.getBigDecimal("cost"), // Just read the stored value
             rs.getInt("duration_days"),
             rs.getBigDecimal("avg_review"),
             rs.getString("hotel_name"),
             rs.getString("transport_type"));
     }
     if (!found) {
         System.out.println("No packages found for your search criteria.");
     }
 }
}

    // Original booking method (without procedure)
    private void bookPackage() throws SQLException {
        System.out.print("Enter Package ID to book: ");
        int packageId = scanner.nextInt();
        System.out.print("Enter Number of People: ");
        int numPeople = scanner.nextInt();

        // Check package availability using FUNCTION
        if (!isPackageAvailable(packageId)) {
            System.out.println("Sorry, this package is not available at the moment.");
            return;
        }

        // Get package cost
        String costQuery = "SELECT cost FROM Package WHERE package_id = ?";
        BigDecimal packageCost = BigDecimal.ZERO;
        try (PreparedStatement stmt = connection.prepareStatement(costQuery)) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                packageCost = rs.getBigDecimal("cost");
            } else {
                System.out.println("Invalid Package ID.");
                return;
            }
        }

        BigDecimal totalCost = packageCost.multiply(BigDecimal.valueOf(numPeople));

        // Apply loyalty discount using FUNCTION
        BigDecimal discount = calculateLoyaltyDiscount(currentUserId);
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            System.out.printf("Loyalty discount applied: $%.2f\n", discount);
            totalCost = totalCost.subtract(discount);
        }

        System.out.printf("Total Cost: $%.2f\n", totalCost);

        // Process payment
        System.out.print("Enter Payment Method (UPI/Credit Card/Debit Card/Cash): ");
        String paymentMethod = scanner.next();

        // Create booking (trigger will calculate cost automatically)
        String bookingQuery = "INSERT INTO Booking (user_id, package_id, num_people, status) VALUES (?, ?, ?, 'Confirmed')";
        try (PreparedStatement stmt = connection.prepareStatement(bookingQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, packageId);
            stmt.setInt(3, numPeople);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int bookingId = generatedKeys.getInt(1);
                
                // Get the calculated total cost (from trigger)
                String costCheckQuery = "SELECT total_cost FROM Booking WHERE booking_id = ?";
                try (PreparedStatement costStmt = connection.prepareStatement(costCheckQuery)) {
                    costStmt.setInt(1, bookingId);
                    ResultSet costRs = costStmt.executeQuery();
                    if (costRs.next()) {
                        totalCost = costRs.getBigDecimal("total_cost");
                    }
                }
                
                // Create payment record
                String paymentQuery = "INSERT INTO Payment (booking_id, amount, method) VALUES (?, ?, ?)";
                try (PreparedStatement paymentStmt = connection.prepareStatement(paymentQuery)) {
                    paymentStmt.setInt(1, bookingId);
                    paymentStmt.setBigDecimal(2, totalCost);
                    paymentStmt.setString(3, paymentMethod);
                    paymentStmt.executeUpdate();
                }

                System.out.println("Booking confirmed! Booking ID: " + bookingId);
                System.out.println("Loyalty points will be updated automatically!");
            }
        }
    }

    private void viewMyBookings() throws SQLException {
        String query = "SELECT b.booking_id, p.from_location, p.to_location, b.num_people, " +
                      "b.total_cost, b.status, b.booking_date " +
                      "FROM Booking b " +
                      "JOIN Package p ON b.package_id = p.package_id " +
                      "WHERE b.user_id = ? " +
                      "ORDER BY b.booking_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- My Bookings ---");
            System.out.println("BookingID | From | To | People | Total Cost | Status | Booking Date");
            System.out.println("-------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-10s %-10s %-7d $%-10.2f %-12s %s\n",
                    rs.getInt("booking_id"),
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getInt("num_people"),
                    rs.getBigDecimal("total_cost"),
                    rs.getString("status"),
                    rs.getTimestamp("booking_date"));
            }
            if (!found) {
                System.out.println("No bookings found.");
            }
        }
    }

    private void addReview() throws SQLException {
        // Show completed trips
        String completedTripsQuery = "SELECT b.booking_id, p.package_id, p.from_location, p.to_location " +
                                   "FROM Booking b " +
                                   "JOIN Package p ON b.package_id = p.package_id " +
                                   "WHERE b.user_id = ? AND b.status = 'Completed'";
        
        try (PreparedStatement stmt = connection.prepareStatement(completedTripsQuery)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Completed Trips Available for Review ---");
            List<Integer> packageIds = new ArrayList<>();
            while (rs.next()) {
                packageIds.add(rs.getInt("package_id"));
                System.out.printf("PackageID: %d | From: %s | To: %s | BookingID: %d\n",
                    rs.getInt("package_id"),
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getInt("booking_id"));
            }

            if (packageIds.isEmpty()) {
                System.out.println("No completed trips available for review.");
                return;
            }

            System.out.print("Enter Package ID to review: ");
            int packageId = scanner.nextInt();

            if (!packageIds.contains(packageId)) {
                System.out.println("Invalid Package ID or you haven't completed this trip.");
                return;
            }

            System.out.print("Enter Rating (0.0 - 5.0): ");
            BigDecimal rating = scanner.nextBigDecimal();
            scanner.nextLine(); // consume newline
            System.out.print("Enter Comment: ");
            String comment = scanner.nextLine();

            String reviewQuery = "INSERT INTO Review (user_id, package_id, rating, comment) VALUES (?, ?, ?, ?)";
            try (PreparedStatement reviewStmt = connection.prepareStatement(reviewQuery)) {
                reviewStmt.setInt(1, currentUserId);
                reviewStmt.setInt(2, packageId);
                reviewStmt.setBigDecimal(3, rating);
                reviewStmt.setString(4, comment);
                reviewStmt.executeUpdate();
                System.out.println("Review added successfully! Package rating will be updated automatically.");
            }
        }
    }

    private void viewMyReviews() throws SQLException {
        String query = "SELECT r.package_id, p.from_location, p.to_location, r.rating, r.comment, r.review_date " +
                      "FROM Review r " +
                      "JOIN Package p ON r.package_id = p.package_id " +
                      "WHERE r.user_id = ? " +
                      "ORDER BY r.review_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- My Reviews ---");
            System.out.println("PackageID | From | To | Rating | Comment | Review Date");
            System.out.println("-----------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-10s %-10s %-6.1f %-20s %s\n",
                    rs.getInt("package_id"),
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getBigDecimal("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("review_date"));
            }
            if (!found) {
                System.out.println("No reviews found.");
            }
        }
    }

    private void viewLoyaltyPoints() throws SQLException {
        String query = "SELECT loyalty_points FROM Customer WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Your Loyalty Points: " + rs.getInt("loyalty_points"));
                System.out.println("You earn 1 point for every $10 spent!");
            }
        }
    }
 // ==================== ADMIN MANAGEMENT METHODS ====================

    private void viewAllCustomers() throws SQLException {
        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, c.loyalty_points " +
                      "FROM User u " +
                      "JOIN Customer c ON u.user_id = c.customer_id " +
                      "WHERE u.role = 'customer'";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- All Customers ---");
            System.out.println("UserID | Name | Email | Phone | Loyalty Points");
            System.out.println("------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-7d %-15s %-20s %-12s %d\n",
                    rs.getInt("user_id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("loyalty_points"));
            }
        }
    }

    private void viewCustomerBookingHistory() throws SQLException {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();

        String query = "SELECT b.booking_id, p.from_location, p.to_location, b.num_people, " +
                      "b.total_cost, b.status, b.booking_date " +
                      "FROM Booking b " +
                      "JOIN Package p ON b.package_id = p.package_id " +
                      "WHERE b.user_id = ? " +
                      "ORDER BY b.booking_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Customer Booking History ---");
            System.out.println("BookingID | From | To | People | Total Cost | Status | Booking Date");
            System.out.println("-------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-10s %-10s %-7d $%-10.2f %-12s %s\n",
                    rs.getInt("booking_id"),
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getInt("num_people"),
                    rs.getBigDecimal("total_cost"),
                    rs.getString("status"),
                    rs.getTimestamp("booking_date"));
            }
            if (!found) {
                System.out.println("No bookings found for this customer.");
            }
        }
    }

    private void viewAllBookings() throws SQLException {
        String query = "SELECT b.booking_id, u.first_name, u.last_name, p.from_location, p.to_location, " +
                      "b.num_people, b.total_cost, b.status, b.booking_date " +
                      "FROM Booking b " +
                      "JOIN User u ON b.user_id = u.user_id " +
                      "JOIN Package p ON b.package_id = p.package_id " +
                      "ORDER BY b.booking_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- All Bookings ---");
            System.out.println("BookingID | Customer | From | To | People | Total Cost | Status | Booking Date");
            System.out.println("------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-12s %-10s %-10s %-7d $%-10.2f %-12s %s\n",
                    rs.getInt("booking_id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getInt("num_people"),
                    rs.getBigDecimal("total_cost"),
                    rs.getString("status"),
                    rs.getTimestamp("booking_date"));
            }
        }
    }

    private void updateBookingStatus() throws SQLException {
        System.out.print("Enter Booking ID: ");
        int bookingId = scanner.nextInt();
        
        System.out.print("Enter New Status (Pending/Confirmed/Completed/Cancelled): ");
        String newStatus = scanner.next();

        String query = "UPDATE Booking SET status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Booking status updated successfully!");
            } else {
                System.out.println("Booking not found.");
            }
        }
    }

    // Destination Management
    private void manageDestinations() throws SQLException {
        while (true) {
            System.out.println("\n--- Destination Management ---");
            System.out.println("1. Add Destination");
            System.out.println("2. View All Destinations");
            System.out.println("3. Update Destination");
            System.out.println("4. Delete Destination");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> addDestination();
                case 2 -> viewAllDestinations();
                case 3 -> updateDestination();
                case 4 -> deleteDestination();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addDestination() throws SQLException {
        scanner.nextLine(); // consume newline
        System.out.print("Enter Destination Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Country: ");
        String country = scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

        String query = "INSERT INTO Destination (name, country, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, country);
            stmt.setString(3, description);
            stmt.executeUpdate();
            System.out.println("Destination added successfully!");
        }
    }

    private void updateDestination() throws SQLException {
        System.out.print("Enter Destination ID to update: ");
        int destId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Enter New Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter New Country: ");
        String country = scanner.nextLine();
        System.out.print("Enter New Description: ");
        String description = scanner.nextLine();

        String query = "UPDATE Destination SET name = ?, country = ?, description = ? WHERE dest_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, country);
            stmt.setString(3, description);
            stmt.setInt(4, destId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Destination updated successfully!");
            } else {
                System.out.println("Destination not found.");
            }
        }
    }

    private void deleteDestination() throws SQLException {
        System.out.print("Enter Destination ID to delete: ");
        int destId = scanner.nextInt();

        String query = "DELETE FROM Destination WHERE dest_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, destId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Destination deleted successfully!");
            } else {
                System.out.println("Destination not found.");
            }
        }
    }

    // Hotel Management
    private void manageHotels() throws SQLException {
        while (true) {
            System.out.println("\n--- Hotel Management ---");
            System.out.println("1. Add Hotel");
            System.out.println("2. View All Hotels");
            System.out.println("3. Update Hotel");
            System.out.println("4. Delete Hotel");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> addHotel();
                case 2 -> viewAllHotels();
                case 3 -> updateHotel();
                case 4 -> deleteHotel();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addHotel() throws SQLException {
        System.out.print("Enter Destination ID: ");
        int destId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter Hotel Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Hotel Type (Budget/Standard/Luxury): ");
        String type = scanner.next();
        System.out.print("Enter Price Per Night: ");
        BigDecimal price = scanner.nextBigDecimal();

        String query = "INSERT INTO Hotel (dest_id, name, type, price_per_night) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, destId);
            stmt.setString(2, name);
            stmt.setString(3, type);
            stmt.setBigDecimal(4, price);
            stmt.executeUpdate();
            System.out.println("Hotel added successfully!");
        }
    }

    private void viewAllHotels() throws SQLException {
        String query = "SELECT h.hotel_id, h.name, h.type, h.price_per_night, d.name as destination_name " +
                      "FROM Hotel h JOIN Destination d ON h.dest_id = d.dest_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- All Hotels ---");
            System.out.println("HotelID | Name | Type | Price/Night | Destination");
            System.out.println("-------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-8d %-15s %-10s $%-12.2f %s\n",
                    rs.getInt("hotel_id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getBigDecimal("price_per_night"),
                    rs.getString("destination_name"));
            }
        }
    }

    private void updateHotel() throws SQLException {
        System.out.print("Enter Hotel ID to update: ");
        int hotelId = scanner.nextInt();
        System.out.print("Enter New Price Per Night: ");
        BigDecimal newPrice = scanner.nextBigDecimal();

        String query = "UPDATE Hotel SET price_per_night = ? WHERE hotel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, newPrice);
            stmt.setInt(2, hotelId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Hotel updated successfully!");
            } else {
                System.out.println("Hotel not found.");
            }
        }
    }

    private void deleteHotel() throws SQLException {
        System.out.print("Enter Hotel ID to delete: ");
        int hotelId = scanner.nextInt();

        String query = "DELETE FROM Hotel WHERE hotel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, hotelId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Hotel deleted successfully!");
            } else {
                System.out.println("Hotel not found.");
            }
        }
    }

    // Transport Management
    private void manageTransport() throws SQLException {
        while (true) {
            System.out.println("\n--- Transport Management ---");
            System.out.println("1. Add Transport");
            System.out.println("2. View All Transport");
            System.out.println("3. Update Transport");
            System.out.println("4. Delete Transport");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> addTransport();
                case 2 -> viewAllTransport();
                case 3 -> updateTransport();
                case 4 -> deleteTransport();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addTransport() throws SQLException {
        System.out.print("Enter Transport Type (Bus/Train/Flight): ");
        String type = scanner.next();
        scanner.nextLine(); // consume newline
        System.out.print("Enter Source: ");
        String source = scanner.nextLine();
        System.out.print("Enter Destination: ");
        String destination = scanner.nextLine();
        System.out.print("Enter Price: ");
        BigDecimal price = scanner.nextBigDecimal();
        scanner.nextLine(); // consume newline
        System.out.print("Enter Departure Time (YYYY-MM-DD HH:MM:SS): ");
        String departureTime = scanner.nextLine();

        String query = "INSERT INTO Transport (type, source, destination, price, departure_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setString(2, source);
            stmt.setString(3, destination);
            stmt.setBigDecimal(4, price);
            stmt.setString(5, departureTime);
            stmt.executeUpdate();
            System.out.println("Transport added successfully!");
        }
    }

    private void viewAllTransport() throws SQLException {
        String query = "SELECT transport_id, type, source, destination, price, departure_time FROM Transport";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- All Transport ---");
            System.out.println("TransportID | Type | Source | Destination | Price | Departure Time");
            System.out.println("---------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-12d %-6s %-10s %-12s $%-7.2f %s\n",
                    rs.getInt("transport_id"),
                    rs.getString("type"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getBigDecimal("price"),
                    rs.getTimestamp("departure_time"));
            }
        }
    }

    private void updateTransport() throws SQLException {
        System.out.print("Enter Transport ID to update: ");
        int transportId = scanner.nextInt();
        System.out.print("Enter New Price: ");
        BigDecimal newPrice = scanner.nextBigDecimal();

        String query = "UPDATE Transport SET price = ? WHERE transport_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, newPrice);
            stmt.setInt(2, transportId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Transport updated successfully!");
            } else {
                System.out.println("Transport not found.");
            }
        }
    }

    private void deleteTransport() throws SQLException {
        System.out.print("Enter Transport ID to delete: ");
        int transportId = scanner.nextInt();

        String query = "DELETE FROM Transport WHERE transport_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, transportId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Transport deleted successfully!");
            } else {
                System.out.println("Transport not found.");
            }
        }
    }

    // Package Management
    private void managePackages() throws SQLException {
        while (true) {
            System.out.println("\n--- Package Management ---");
            System.out.println("1. Add Package");
            System.out.println("2. View All Packages");
            System.out.println("3. Update Package");
            System.out.println("4. Delete Package");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> addPackage();
                case 2 -> viewAllPackages();
                case 3 -> updatePackage();
                case 4 -> deletePackage();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addPackage() throws SQLException {
        System.out.print("Enter From Location: ");
        String fromLocation = scanner.next();
        System.out.print("Enter To Location: ");
        String toLocation = scanner.next();
        System.out.print("Enter Hotel ID: ");
        int hotelId = scanner.nextInt();
        System.out.print("Enter Transport ID: ");
        int transportId = scanner.nextInt();
        System.out.print("Enter Duration (days): ");
        int duration = scanner.nextInt();
        System.out.print("Enter Total Cost: ");
        BigDecimal cost = scanner.nextBigDecimal();

        String query = "INSERT INTO Package (from_location, to_location, hotel_id, transport_id, duration_days, cost) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fromLocation);
            stmt.setString(2, toLocation);
            stmt.setInt(3, hotelId);
            stmt.setInt(4, transportId);
            stmt.setInt(5, duration);
            stmt.setBigDecimal(6, cost);
            stmt.executeUpdate();
            System.out.println("Package added successfully!");
        }
    }

    private void viewAllPackages() throws SQLException {
        String query = "SELECT p.package_id, p.from_location, p.to_location, p.duration_days, p.cost, " +
                      "p.avg_review, h.name as hotel_name, t.type as transport_type " +
                      "FROM Package p " +
                      "JOIN Hotel h ON p.hotel_id = h.hotel_id " +
                      "JOIN Transport t ON p.transport_id = t.transport_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- All Packages ---");
            System.out.println("PackageID | From | To | Days | Cost | Rating | Hotel | Transport");
            System.out.println("----------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-10s %-10s %-5d $%-7.2f %-6.1f %-15s %-10s\n",
                    rs.getInt("package_id"),
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getInt("duration_days"),
                    rs.getBigDecimal("cost"),
                    rs.getBigDecimal("avg_review"),
                    rs.getString("hotel_name"),
                    rs.getString("transport_type"));
            }
        }
    }

    private void updatePackage() throws SQLException {
        System.out.print("Enter Package ID to update: ");
        int packageId = scanner.nextInt();
        System.out.print("Enter New Cost: ");
        BigDecimal newCost = scanner.nextBigDecimal();

        String query = "UPDATE Package SET cost = ? WHERE package_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, newCost);
            stmt.setInt(2, packageId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Package updated successfully!");
            } else {
                System.out.println("Package not found.");
            }
        }
    }

    private void deletePackage() throws SQLException {
        System.out.print("Enter Package ID to delete: ");
        int packageId = scanner.nextInt();

        String query = "DELETE FROM Package WHERE package_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, packageId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Package deleted successfully!");
            } else {
                System.out.println("Package not found.");
            }
        }
    }

    // ==================== ADMIN METHODS ====================
    private void showAdminMenu() throws SQLException {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("           ADMIN MENU            ");
            System.out.println("=================================");
            System.out.printf("%-3s%-25s%n", "1.", "View All Customers");
            System.out.printf("%-3s%-25s%n", "2.", "View Customer Booking History");
            System.out.printf("%-3s%-25s%n", "3.", "View All Bookings");
            System.out.printf("%-3s%-25s%n", "4.", "Update Booking Status");
            System.out.printf("%-3s%-25s%n", "5.", "Manage Destinations");
            System.out.printf("%-3s%-25s%n", "6.", "Manage Hotels");
            System.out.printf("%-3s%-25s%n", "7.", "Manage Transport");
            System.out.printf("%-3s%-25s%n", "8.", "Manage Packages");
            System.out.printf("%-3s%-25s%n", "0.", "Logout");
            System.out.println("=================================");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> viewAllCustomers();
                case 2 -> viewCustomerBookingHistory();
                case 3 -> viewAllBookings();
                case 4 -> updateBookingStatus();
                case 5 -> manageDestinations();
                case 6 -> manageHotels();
                case 7 -> manageTransport();
                case 8 -> managePackages();
                case 0 -> {
                    System.out.println("Logging out...");
                    currentUserId = -1;
                    currentUserRole = "";
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ... (Admin methods remain the same as previous version)

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        TravelBookingSystem system = new TravelBookingSystem();

        try {
            while (true) {
                System.out.println("\n=================================");
                System.out.println("     TRAVEL BOOKING SYSTEM      ");
                System.out.println("=================================");
                System.out.printf("%-3s%-25s%n", "1.", "Register");
                System.out.printf("%-3s%-25s%n", "2.", "Login");
                System.out.printf("%-3s%-25s%n", "0.", "Exit");
                System.out.println("=================================");
                System.out.print("Choose an option: ");

                int choice = system.scanner.nextInt();
                switch (choice) {
                    case 1 -> system.registerUser();
                    case 2 -> system.login();
                    case 0 -> {
                        System.out.println("Thank you for using Travel Booking System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}