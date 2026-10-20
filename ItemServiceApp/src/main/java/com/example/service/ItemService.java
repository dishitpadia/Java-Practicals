package com.example.service;

import com.example.model.Item;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private final ArrayList<Item> items = new ArrayList<>();

    public void addItem(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        items.add(new Item(name));
    }

    public int getCount() {
        return items.size();
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public void performAction(String action, String name) {
        if ("ADD".equals(action)) {
            addItem(name);
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filename))) {

            out.writeObject(items);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(filename))) {

            Object data = in.readObject();

            if (!(data instanceof ArrayList<?>)) {
                throw new IOException("Invalid item data format");
            }

            ArrayList<?> loadedItems = (ArrayList<?>) data;

            for (Object item : loadedItems) {
                if (!(item instanceof Item)) {
                    throw new IOException("Invalid item in saved data");
                }
            }

            items.clear();
            items.addAll((ArrayList<Item>) loadedItems);
        }
    }
}