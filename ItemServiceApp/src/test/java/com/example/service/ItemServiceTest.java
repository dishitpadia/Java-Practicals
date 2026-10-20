package com.example.service;

import com.example.model.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testAddingItemIncreasesCount() {
        ItemService service = new ItemService();

        assertEquals(0, service.getCount());

        service.addItem("Book");

        assertEquals(1, service.getCount());
    }

    @Test
    public void testAddingMultipleItems() {
        ItemService service = new ItemService();

        service.addItem("Book");
        service.addItem("Pen");
        service.addItem("Notebook");

        assertEquals(3, service.getCount());
    }

    @Test
    public void testInvalidActionThrowsException() {
        ItemService service = new ItemService();

        assertThrows(
            IllegalArgumentException.class,
            () -> service.performAction("DELETE", "Book")
        );
    }

    @Test
    public void testSaveAndReloadDataUnchanged()
            throws IOException, ClassNotFoundException {

        ItemService originalService = new ItemService();

        originalService.addItem("Book");
        originalService.addItem("Pen");
        originalService.addItem("Notebook");

        Path file = tempDir.resolve("items.dat");

        originalService.saveToFile(file.toString());

        ItemService loadedService = new ItemService();
        loadedService.loadFromFile(file.toString());

        assertEquals(
            originalService.getCount(),
            loadedService.getCount()
        );

        List<Item> originalItems = originalService.getItems();
        List<Item> loadedItems = loadedService.getItems();

        assertEquals(originalItems.size(), loadedItems.size());

        for (int i = 0; i < originalItems.size(); i++) {
            assertEquals(
                originalItems.get(i).getName(),
                loadedItems.get(i).getName()
            );
        }
    }
}