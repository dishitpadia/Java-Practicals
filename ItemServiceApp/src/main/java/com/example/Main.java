package com.example;

import com.example.model.Item;
import com.example.service.ItemService;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ItemService service = new ItemService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Item Service App ---");
            System.out.println("1. Add Item");
            System.out.println("2. Display Items");
            System.out.println("3. Display Count");
            System.out.println("4. Save Items");
            System.out.println("5. Load Items");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter item name: ");
                        String name = sc.nextLine();
                        service.addItem(name);
                        System.out.println("Item added successfully.");
                        break;

                    case 2:
                        System.out.println("Items:");

                        for (Item item : service.getItems()) {
                            System.out.println(item);
                        }

                        break;

                    case 3:
                        System.out.println("Total items: " + service.getCount());
                        break;

                    case 4:
                        service.saveToFile("items.dat");
                        System.out.println("Items saved successfully.");
                        break;

                    case 5:
                        service.loadFromFile("items.dat");
                        System.out.println("Items loaded successfully.");
                        break;

                    case 6:
                        System.out.println("Exiting application.");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("File operation failed: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}