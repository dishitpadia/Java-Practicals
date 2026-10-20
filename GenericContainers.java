class Box<T> {
    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}

class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}

public class GenericContainers {

    public static <T> T getFirstElement(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }

        return array[0];
    }

    public static void main(String[] args) {

        Box<Integer> intBox = new Box<>();
        intBox.set(100);
        System.out.println("Box Integer: " + intBox.get());

        Box<String> strBox = new Box<>();
        strBox.set("Hello Java");
        System.out.println("Box String: " + strBox.get());

        Pair<String, Integer> pair = new Pair<>("Rahul", 20);
        System.out.println("Pair First: " + pair.getFirst());
        System.out.println("Pair Second: " + pair.getSecond());

        Integer[] numbers = {10, 20, 30};
        String[] names = {"Amit", "Priya", "Rahul"};

        System.out.println("First Number: " + getFirstElement(numbers));
        System.out.println("First Name: " + getFirstElement(names));
    }
}
