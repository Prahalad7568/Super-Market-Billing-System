import java.util.*;
import java.time.LocalDateTime;
import java.text.DecimalFormat;

// Product class to represent items in the supermarket
class Product {
    private String productId;
    private String name;
    private double price;
    private String category;
    private String hsnCode; // Added HSN Code for GST

    public Product(String productId, String name, double price, String category, String hsnCode) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.hsnCode = hsnCode;
    }

    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getHsnCode() { return hsnCode; }
}

// Bill Item class to track individual items in a bill
class BillItem {
    private Product product;
    private int quantity;
    private double totalPrice;
    private double cgstRate;
    private double sgstRate;

    public BillItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
        
        // Default GST rates
        this.cgstRate = 0.09; // 9% CGST
        this.sgstRate = 0.09; // 9% SGST
    }

    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
    public double getCgstAmount() { return totalPrice * cgstRate; }
    public double getSgstAmount() { return totalPrice * sgstRate; }
}

// Bill class to manage bill generation and details
class Bill {
    private String billId;
    private List<BillItem> items;
    private LocalDateTime billDate;
    private double totalAmount;
    private double cgstRate;
    private double sgstRate;

    public Bill() {
        this.billId = generateBillId();
        this.items = new ArrayList<>();
        this.billDate = LocalDateTime.now();
        this.cgstRate = 0.09; // 9% CGST
        this.sgstRate = 0.09; // 9% SGST
    }

    private String generateBillId() {
        return "BILL-" + System.currentTimeMillis();
    }

    public void addItem(Product product, int quantity) {
        items.add(new BillItem(product, quantity));
    }

    public double calculateSubtotal() {
        totalAmount = items.stream()
                           .mapToDouble(BillItem::getTotalPrice)
                           .sum();
        return totalAmount;
    }

    public double calculateTotalCGST() {
        return items.stream()
                    .mapToDouble(BillItem::getCgstAmount)
                    .sum();
    }

    public double calculateTotalSGST() {
        return items.stream()
                    .mapToDouble(BillItem::getSgstAmount)
                    .sum();
    }

    public double calculateTotalWithGST() {
        return calculateSubtotal() + calculateTotalCGST() + calculateTotalSGST();
    }

    public void printBill() {
        DecimalFormat df = new DecimalFormat("#.##");
        
        System.out.println("===============================");
        System.out.println("SUPERMARKET BILLING SYSTEM");
        System.out.println("Bill ID: " + billId);
        System.out.println("Date: " + billDate);
        System.out.println("-------------------------------");
        System.out.println("ITEM DETAILS:");
        
        for (BillItem item : items) {
            System.out.printf("%-20s %3d x $%-8.2f $%-8.2f%n", 
                item.getProduct().getName(), 
                item.getQuantity(), 
                item.getProduct().getPrice(),
                item.getTotalPrice());
        }
        
        System.out.println("-------------------------------");
        System.out.println("TAX BREAKDOWN:");
        System.out.printf("Subtotal:        $%-8.2f%n", calculateSubtotal());
        System.out.printf("CGST (9%%):       $%-8.2f%n", calculateTotalCGST());
        System.out.printf("SGST (9%%):       $%-8.2f%n", calculateTotalSGST());
        System.out.printf("Total GST:       $%-8.2f%n", calculateTotalCGST() + calculateTotalSGST());
        System.out.printf("Total Bill:      $%-8.2f%n", calculateTotalWithGST());
        System.out.println("===============================");
    }
}

// Inventory Management Class
class Inventory {
    private Map<String, Product> products;

    public Inventory() {
        products = new HashMap<>();
        // Initialize with some default products and their HSN Codes
        addProduct(new Product("P001", "Milk", 3.50, "Dairy", "0404"));
        addProduct(new Product("P002", "Bread", 2.25, "Bakery", "1905"));
        addProduct(new Product("P003", "Eggs", 4.00, "Dairy", "0407"));
        addProduct(new Product("P004", "Cheese", 5.50, "Dairy", "0406"));
        addProduct(new Product("P005", "Apple", 0.50, "Fruits", "0808"));
    }

    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public void listAllProducts() {
        System.out.println("Available Products:");
        products.values().forEach(p -> 
            System.out.printf("%s - %s (HSN: %s): $%.2f%n", 
                p.getProductId(), p.getName(), p.getHsnCode(), p.getPrice())
        );
    }
}

// Main Application Class
public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Supermarket Billing System ---");
            System.out.println("1. View Products");
            System.out.println("2. Create New Bill");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    inventory.listAllProducts();
                    break;
                case 2:
                    createBill(inventory, scanner);
                    break;
                case 3:
                    System.out.println("Thank you for using Supermarket Billing System!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createBill(Inventory inventory, Scanner scanner) {
        Bill bill = new Bill();

        while (true) {
            inventory.listAllProducts();
            System.out.print("Enter Product ID (or 'done' to finish): ");
            String productId = scanner.nextLine();

            if (productId.equalsIgnoreCase("done")) {
                break;
            }

            Product product = inventory.getProduct(productId);
            if (product == null) {
                System.out.println("Invalid Product ID!");
                continue;
            }

            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            bill.addItem(product, quantity);
        }

        bill.printBill();
    }
}
