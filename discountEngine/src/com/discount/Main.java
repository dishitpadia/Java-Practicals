package discountEngine.src.com.discount;

import java.util.*;
import discountEngine.src.com.discount.model.DiscountRule;
import discountEngine.src.com.discount.service.DiscountService;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        List<Double> prices = Arrays.asList(1000.0, 2000.0, 3000.0, 4000.0);

        DiscountRule rule;

        System.out.println("===== DISCOUNT ENGINE =====");
        System.out.println("Original Prices: " + prices);

        System.out.println("\nSelect Discount Rule:");
        System.out.println("1. 10% Discount");
        System.out.println("2. 20% Discount");
        System.out.println("3. Flat Rs. 100 Discount");
        System.out.println("4. No Discount");

        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                rule = price -> price * 0.90;
                break;

            case 2:
                rule = price -> price * 0.80;
                break;

            case 3:
                rule = price -> Math.max(0, price - 100);
                break;

            case 4:
                rule = price -> price;
                break;

            default:
                System.out.println("Invalid choice!");
                sc.close();
                return;
        }

        DiscountService service = new DiscountService();

        List<Double> result = service.applyDiscount(prices, rule);

        System.out.println("\nDiscounted Prices:");

        for (int i = 0; i < prices.size(); i++) {
            System.out.printf("Rs. %.2f -> Rs. %.2f%n",
                    prices.get(i), result.get(i));
        }

        sc.close();
    }
}
