import java.util.List;

class OutofStockException extends Exception {

    OutofStockException(String message) {
        super(message);
    }
}

class InvalidQuantityException extends Exception {

    InvalidQuantityException(String message) {
        super(message);
    }
}

class Stock {

    int quantity;

    Stock(int quantity) {
        this.quantity = quantity;
    }

    public void issue(String item, int qty)
            throws OutofStockException, InvalidQuantityException {

        if (qty <= 0) {
            throw new InvalidQuantityException("The item quantity is invalid");
        }

        if (qty > quantity) {
            throw new OutofStockException("The item is out of stock");
        }

        quantity -= qty;
        IO.println(item + " issued successfully. Remaining stock: " + quantity);
    }

}

void main() {

    Stock stock = new Stock(50);

    List<String> items = List.of("Laptop", "Keyboard", "Mouse", "Monitor");
    List<Integer> quantities = List.of(10, 60, 0, 20);

    for (int i = 0; i < items.size(); ++i) {
        try {
            stock.issue(items.get(i), quantities.get(i));

        } catch (OutofStockException e) {
            IO.println("Out of stock: " + e.getMessage());
        } catch (InvalidQuantityException e) {
            IO.println("Invalid quantity: " + e.getMessage());
        }

    }

    IO.println("All requests processed");
}
